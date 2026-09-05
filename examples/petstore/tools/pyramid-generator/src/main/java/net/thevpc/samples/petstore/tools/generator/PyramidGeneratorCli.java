package net.thevpc.samples.petstore.tools.generator;

import net.thevpc.nuts.app.NApp;
import net.thevpc.nuts.app.NApplication;
import net.thevpc.nuts.app.NAppRun;
import net.thevpc.nuts.cmdline.NArg;
import net.thevpc.nuts.cmdline.NCmdLine;
import net.thevpc.nuts.text.NMsg;
import net.thevpc.nuts.util.NOptional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@NApp
public class PyramidGeneratorCli {

    public static void main(String[] args) {
        new PyramidGeneratorCli().run(args);
    }

    @NAppRun
    public void run(String[] args) {
        String projectRoot = ".";
        boolean generateTools = false;

        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                if ("-r".equals(args[i]) || "--root".equals(args[i])) {
                    if (i + 1 < args.length) {
                        projectRoot = args[++i];
                    }
                } else if ("-t".equals(args[i]) || "--tools".equals(args[i])) {
                    generateTools = true;
                }
            }
        }

        System.out.println(String.format("Pyramid Generator scanning root: %s", projectRoot));
        Path root = Paths.get(projectRoot);
        PyramidCodeGenerator generator = new PyramidCodeGenerator();

        List<Path> facades = new ArrayList<>();
        try {
            Files.walk(root)
                    .filter(p -> p.toString().endsWith("Module.java") && p.toString().contains("service-api"))
                    .forEach(facades::add);

            System.out.println(NMsg.ofC("Found %d facade interfaces.", facades.size()));

            for (Path facade : facades) {
                System.out.println(NMsg.ofC("Analyzing facade: %s", facade.getFileName()));
                // Find matching ws-rest path
                String wsPathStr = facade.toString()
                        .replace("-service-api", "-ws-rest")
                        .replace("/service/api/", "/ws/rest/")
                        .replace(File.separator + "service" + File.separator + "api" + File.separator, File.separator + "ws" + File.separator + "rest" + File.separator)
                        .replace("Module.java", "WS.java");
                Path wsPath = Paths.get(wsPathStr);
                boolean modified = generator.syncWsController(facade, wsPath);
                if (modified) {
                    System.out.println(NMsg.ofC("  [SYNCED] Updated %s losslessly.", wsPath.getFileName()));
                } else {
                    System.out.println(NMsg.ofC("  [UP-TO-DATE] %s is already synchronized.", wsPath.getFileName()));
                }
            }

            if (generateTools || !facades.isEmpty()) {
                Path catalogOut = root.resolve("tool-catalog.json");
                String catalogJson = generator.generateAiToolCatalog(facades);
                Files.writeString(catalogOut, catalogJson);
                System.out.println(NMsg.ofC("##AI Tool Catalog## generated at: %s", catalogOut.toAbsolutePath()));
            }
        } catch (IOException e) {
            System.out.println(NMsg.ofC("[ERROR] Code generation failed: %s", e.getMessage()));
        }
    }
}
