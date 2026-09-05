package net.thevpc.samples.petstore.test;

import net.thevpc.samples.petstore.app.PetStoreApplication;
import net.thevpc.samples.petstore.core.actiongraph.ActionExecutionResult;
import net.thevpc.samples.petstore.core.actiongraph.ActionGraph;
import net.thevpc.samples.petstore.core.actiongraph.ActionGraphDispatcher;
import net.thevpc.samples.petstore.core.actiongraph.ActionNode;
import net.thevpc.samples.petstore.modules.catalog.infra.Category;
import net.thevpc.samples.petstore.modules.catalog.infra.Pet;
import net.thevpc.samples.petstore.modules.catalog.infra.PetStatus;
import net.thevpc.samples.petstore.modules.catalog.service.api.PetCatalogModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = PetStoreApplication.class)
public class ActionGraphDispatcherTest {

    @Autowired
    private ActionGraphDispatcher actionGraphDispatcher;

    @Autowired
    private PetCatalogModule petCatalogModule;

    @Test
    @DisplayName("Face 4 (AI Action Graph): Dispatcher executes approved DAG against module facade under AI control")
    public void testActionGraphExecution() {
        ActionGraph graph = new ActionGraph("graph-restock-1", "AI Agent restocking low-inventory pets");

        Pet newPet = new Pet("pet-ai-1", "Siamese Kitten", new Category("cats", "Cats"),
                PetStatus.AVAILABLE, new BigDecimal("350.00"));

        ActionNode node1 = new ActionNode(
                "node-add-pet",
                "petCatalogModule",
                "addPet",
                Map.of("pet", newPet),
                "Restock inventory due to high customer search volume",
                0.96,
                List.of()
        );
        node1.setApproved(true);

        graph.addNode(node1);

        List<ActionExecutionResult> results = actionGraphDispatcher.executeGraph(graph);

        assertEquals(1, results.size());
        assertTrue(results.get(0).isSuccess());

        Optional<Pet> loaded = petCatalogModule.findPetById("pet-ai-1");
        assertTrue(loaded.isPresent());
        assertEquals("Siamese Kitten", loaded.get().getName());
    }
}
