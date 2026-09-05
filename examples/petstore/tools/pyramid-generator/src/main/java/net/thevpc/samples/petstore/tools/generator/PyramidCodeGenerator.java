package net.thevpc.samples.petstore.tools.generator;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JavaParser-based code generator for The Pyramid Architecture.
 * Inspects facade interfaces and synchronizes WS controllers losslessly (AST-based),
 * preserving all pre-existing hand-written methods, comments, and annotations.
 * Also emits AI Agent tool catalogs (OpenAPI/JSON Schema specifications).
 */
public class PyramidCodeGenerator {

    static {
        StaticJavaParser.getParserConfiguration().setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17);
    }

    /**
     * Losslessly synchronizes a WS controller with its facade interface.
     * Only missing methods are injected; existing methods are preserved untouched.
     */
    public boolean syncWsController(Path facadeInterfacePath, Path wsControllerPath) throws IOException {
        if (!Files.exists(facadeInterfacePath)) {
            throw new IllegalArgumentException("Facade file does not exist: " + facadeInterfacePath);
        }

        CompilationUnit facadeCu = StaticJavaParser.parse(facadeInterfacePath);
        Optional<ClassOrInterfaceDeclaration> facadeOpt = facadeCu.findFirst(ClassOrInterfaceDeclaration.class, ClassOrInterfaceDeclaration::isInterface);
        if (facadeOpt.isEmpty()) {
            return false;
        }
        ClassOrInterfaceDeclaration facadeDecl = facadeOpt.get();
        String facadeName = facadeDecl.getNameAsString();

        CompilationUnit wsCu;
        ClassOrInterfaceDeclaration wsClass;
        boolean createdNew = false;

        if (Files.exists(wsControllerPath)) {
            wsCu = StaticJavaParser.parse(wsControllerPath);
            wsClass = wsCu.findFirst(ClassOrInterfaceDeclaration.class, c -> !c.isInterface())
                    .orElseThrow(() -> new IllegalStateException("No class found in " + wsControllerPath));
        } else {
            createdNew = true;
            wsCu = new CompilationUnit();
            String pkg = facadeCu.getPackageDeclaration().map(p -> p.getNameAsString().replace(".service.api", ".ws.rest")).orElse("net.thevpc.samples");
            wsCu.setPackageDeclaration(pkg);
            wsCu.addImport("org.springframework.web.bind.annotation.*");
            wsCu.addImport("org.springframework.beans.factory.annotation.Autowired");
            wsCu.addImport("org.springframework.http.ResponseEntity");
            wsCu.addImport("net.thevpc.samples.petstore.core.infra.annotation.AppModuleWS");
            wsCu.addImport("net.thevpc.samples.petstore.core.infra.annotation.Generated");
            wsCu.addImport(facadeDecl.getFullyQualifiedName().orElse(""));

            wsClass = wsCu.addClass(facadeName.replace("Module", "WS"));
            wsClass.addAnnotation("RestController");
            wsClass.addSingleMemberAnnotation("AppModuleWS", facadeName + ".class");
            wsClass.addSingleMemberAnnotation("Generated", "\"pyramid\"");

            // Add autowired field
            String fieldName = Character.toLowerCase(facadeName.charAt(0)) + facadeName.substring(1);
            wsClass.addField(facadeName, fieldName, Modifier.Keyword.PRIVATE).addAnnotation("Autowired");
        }

        boolean modified = createdNew;
        String fieldName = Character.toLowerCase(facadeName.charAt(0)) + facadeName.substring(1);

        for (MethodDeclaration facadeMethod : facadeDecl.getMethods()) {
            // Check if discarded
            boolean discarded = facadeMethod.getAnnotations().stream()
                    .anyMatch(a -> a.getNameAsString().equals("GeneratorDiscard"));
            if (discarded) {
                continue;
            }

            String methodName = facadeMethod.getNameAsString();
            int paramCount = facadeMethod.getParameters().size();

            // Check if already present in wsClass
            boolean alreadyPresent = wsClass.getMethods().stream()
                    .anyMatch(m -> m.getNameAsString().equals(methodName) && m.getParameters().size() == paramCount);

            if (!alreadyPresent) {
                // Synthesize new method into WS
                MethodDeclaration newWsMethod = new MethodDeclaration();
                newWsMethod.setName(methodName);
                newWsMethod.setPublic(true);

                // Add mapping annotation
                if (methodName.startsWith("get") || methodName.startsWith("find")) {
                    newWsMethod.addSingleMemberAnnotation("GetMapping", "\"/" + methodName + "\"");
                } else if (methodName.startsWith("remove") || methodName.startsWith("delete")) {
                    newWsMethod.addSingleMemberAnnotation("DeleteMapping", "\"/" + methodName + "\"");
                } else {
                    newWsMethod.addSingleMemberAnnotation("PostMapping", "\"/" + methodName + "\"");
                }

                // Return type: ResponseEntity<T>
                String returnType = facadeMethod.getTypeAsString();
                if ("void".equals(returnType)) {
                    newWsMethod.setType("ResponseEntity<Void>");
                } else {
                    newWsMethod.setType("ResponseEntity<" + boxType(returnType) + ">");
                }

                // Copy parameters
                List<String> argNames = new ArrayList<>();
                for (Parameter p : facadeMethod.getParameters()) {
                    Parameter wsParam = new Parameter(p.getType(), p.getNameAsString());
                    if (p.getType().isClassOrInterfaceType() && !p.getTypeAsString().equals("String") && !p.getTypeAsString().startsWith("int") && !p.getTypeAsString().startsWith("boolean")) {
                        wsParam.addAnnotation("RequestBody");
                    } else {
                        wsParam.addAnnotation("RequestParam");
                    }
                    newWsMethod.addParameter(wsParam);
                    argNames.add(p.getNameAsString());
                }

                // Body: return ResponseEntity.ok(module.method(...));
                BlockStmt body = new BlockStmt();
                String argsStr = String.join(", ", argNames);
                if ("void".equals(returnType)) {
                    body.addStatement(fieldName + "." + methodName + "(" + argsStr + ");");
                    body.addStatement("return ResponseEntity.ok().build();");
                } else {
                    body.addStatement("return ResponseEntity.ok(" + fieldName + "." + methodName + "(" + argsStr + "));");
                }
                newWsMethod.setBody(body);

                wsClass.addMember(newWsMethod);
                modified = true;
            }
        }

        if (modified) {
            Files.createDirectories(wsControllerPath.getParent());
            Files.writeString(wsControllerPath, wsCu.toString(), StandardCharsets.UTF_8);
        }
        return modified;
    }

    /**
     * Generates an AI Tool Catalog (JSON Schema) from module facade interfaces.
     */
    public String generateAiToolCatalog(List<Path> facadePaths) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n  \"tools\": [\n");

        boolean firstTool = true;
        for (Path path : facadePaths) {
            if (!Files.exists(path)) continue;
            CompilationUnit cu = StaticJavaParser.parse(path);
            Optional<ClassOrInterfaceDeclaration> opt = cu.findFirst(ClassOrInterfaceDeclaration.class, ClassOrInterfaceDeclaration::isInterface);
            if (opt.isEmpty()) continue;
            ClassOrInterfaceDeclaration decl = opt.get();
            String moduleName = decl.getNameAsString();

            for (MethodDeclaration method : decl.getMethods()) {
                if (method.getAnnotations().stream().anyMatch(a -> a.getNameAsString().equals("GeneratorDiscard"))) {
                    continue;
                }
                if (!firstTool) {
                    sb.append(",\n");
                }
                firstTool = false;

                String desc = method.getJavadoc().map(j -> j.getDescription().toText().trim().replace("\n", " "))
                        .orElse("Invokes " + moduleName + "." + method.getNameAsString());

                sb.append("    {\n");
                sb.append("      \"type\": \"function\",\n");
                sb.append("      \"function\": {\n");
                sb.append("        \"name\": \"").append(moduleName).append("_").append(method.getNameAsString()).append("\",\n");
                sb.append("        \"description\": \"").append(desc.replace("\"", "\\\"")).append("\",\n");
                sb.append("        \"module\": \"").append(moduleName).append("\",\n");
                sb.append("        \"parameters\": {\n");
                sb.append("          \"type\": \"object\",\n");
                sb.append("          \"properties\": {\n");

                boolean firstParam = true;
                for (Parameter p : method.getParameters()) {
                    if (!firstParam) sb.append(",\n");
                    firstParam = false;
                    sb.append("            \"").append(p.getNameAsString()).append("\": {\n");
                    sb.append("              \"type\": \"").append(mapJsonType(p.getTypeAsString())).append("\",\n");
                    sb.append("              \"javaType\": \"").append(p.getTypeAsString()).append("\"\n");
                    sb.append("            }");
                }
                sb.append("\n          }\n");
                sb.append("        }\n");
                sb.append("      }\n");
                sb.append("    }");
            }
        }
        sb.append("\n  ]\n}\n");
        return sb.toString();
    }

    private String mapJsonType(String javaType) {
        if ("String".equals(javaType) || "Instant".equals(javaType)) return "string";
        if ("int".equals(javaType) || "Integer".equals(javaType) || "long".equals(javaType) || "Long".equals(javaType)) return "integer";
        if ("double".equals(javaType) || "Double".equals(javaType) || "BigDecimal".equals(javaType)) return "number";
        if ("boolean".equals(javaType) || "Boolean".equals(javaType)) return "boolean";
        if (javaType.startsWith("List") || javaType.startsWith("Set") || javaType.endsWith("[]")) return "array";
        return "object";
    }

    private String boxType(String type) {
        switch (type) {
            case "void": return "Void";
            case "boolean": return "Boolean";
            case "int": return "Integer";
            case "long": return "Long";
            case "double": return "Double";
            case "float": return "Float";
            case "byte": return "Byte";
            case "short": return "Short";
            case "char": return "Character";
            default: return type;
        }
    }
}
