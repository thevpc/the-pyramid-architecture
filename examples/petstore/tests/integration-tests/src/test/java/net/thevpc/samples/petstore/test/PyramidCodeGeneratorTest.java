package net.thevpc.samples.petstore.test;

import net.thevpc.samples.petstore.tools.generator.PyramidCodeGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PyramidCodeGeneratorTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Code Generator: Lossless JavaParser AST sync injects missing methods and preserves existing code")
    public void testLosslessAstSync() throws IOException {
        // 1. Create a dummy facade interface
        Path facadePath = tempDir.resolve("PetSampleModule.java");
        String facadeCode = """
                package net.thevpc.samples.petstore.modules.sample.service.api;
                
                public interface PetSampleModule {
                    String getSampleName(String id);
                    void processSample(String id, int count);
                }
                """;
        Files.writeString(facadePath, facadeCode);

        // 2. Create an existing WS controller with custom hand-written logic
        Path wsPath = tempDir.resolve("PetSampleWS.java");
        String existingWsCode = """
                package net.thevpc.samples.petstore.modules.sample.ws.rest;
                
                import org.springframework.web.bind.annotation.*;
                import org.springframework.http.ResponseEntity;
                import org.springframework.beans.factory.annotation.Autowired;
                import net.thevpc.samples.petstore.modules.sample.service.api.PetSampleModule;
                
                @RestController
                public class PetSampleWS {
                    @Autowired
                    private PetSampleModule petSampleModule;
                    
                    // Hand-written custom implementation
                    @GetMapping("/custom")
                    public ResponseEntity<String> getSampleName(@RequestParam String id) {
                        return ResponseEntity.ok("CUSTOM-" + petSampleModule.getSampleName(id));
                    }
                }
                """;
        Files.writeString(wsPath, existingWsCode);

        // 3. Run Generator
        PyramidCodeGenerator generator = new PyramidCodeGenerator();
        boolean modified = generator.syncWsController(facadePath, wsPath);
        assertTrue(modified, "Generator should have injected missing processSample method");

        String updatedContent = Files.readString(wsPath);
        // Verify custom code was preserved
        assertTrue(updatedContent.contains("CUSTOM-"), "Custom hand-written method must be preserved");
        // Verify missing method was added
        assertTrue(updatedContent.contains("processSample"), "Missing processSample method must be injected");

        // 4. Run generator again -> should detect up-to-date and make no modifications
        boolean modifiedAgain = generator.syncWsController(facadePath, wsPath);
        assertFalse(modifiedAgain, "Second run should make no modifications (idempotent)");
    }

    @Test
    @DisplayName("Convergence / Face 4: Generator emits valid AI Agent Tool Catalog (JSON Schema)")
    public void testAiToolCatalogGeneration() throws IOException {
        Path facadePath = tempDir.resolve("PetCatalogModule.java");
        String facadeCode = """
                package net.thevpc.samples.petstore.modules.catalog.service.api;
                
                import java.util.List;
                
                public interface PetCatalogModule {
                    /**
                     * Adds a new pet to the catalog.
                     */
                    String addPet(String name, double price);
                    
                    /**
                     * Finds pets by name.
                     */
                    List<String> findPets(String name);
                }
                """;
        Files.writeString(facadePath, facadeCode);

        PyramidCodeGenerator generator = new PyramidCodeGenerator();
        String jsonCatalog = generator.generateAiToolCatalog(List.of(facadePath));

        assertTrue(jsonCatalog.contains("\"PetCatalogModule_addPet\""));
        assertTrue(jsonCatalog.contains("\"PetCatalogModule_findPets\""));
        assertTrue(jsonCatalog.contains("\"type\": \"function\""));
        assertTrue(jsonCatalog.contains("\"description\": \"Adds a new pet to the catalog.\""));
    }
}
