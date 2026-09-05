package net.thevpc.samples.petstore.test;

import net.thevpc.samples.petstore.app.PetStoreApplication;
import net.thevpc.samples.petstore.core.infra.exception.ValidationException;
import net.thevpc.samples.petstore.core.interceptor.api.InterceptorScope;
import net.thevpc.samples.petstore.drivers.notification.memory.dal.api.NotificationLogRepo;
import net.thevpc.samples.petstore.drivers.notification.memory.infra.NotificationLog;
import net.thevpc.samples.petstore.modules.catalog.infra.Category;
import net.thevpc.samples.petstore.modules.catalog.infra.Pet;
import net.thevpc.samples.petstore.modules.catalog.infra.PetStatus;
import net.thevpc.samples.petstore.modules.catalog.service.api.PetCatalogModule;
import net.thevpc.samples.petstore.modules.order.infra.Order;
import net.thevpc.samples.petstore.modules.order.infra.OrderItem;
import net.thevpc.samples.petstore.modules.order.infra.OrderStatus;
import net.thevpc.samples.petstore.modules.order.service.api.PetOrderModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = PetStoreApplication.class)
public class PetStoreEndToEndTest {

    @Autowired
    private PetCatalogModule petCatalogModule;

    @Autowired
    private PetOrderModule petOrderModule;

    @Autowired
    private NotificationLogRepo notificationLogRepo;

    @Test
    @DisplayName("End-to-End: Create Pet, Place Order, trigger BEFORE_ADD validation, AFTER_ADD pet status sync and notification")
    public void testSuccessfulOrderPlacementWithInterceptors() {
        // 1. Add available pet
        Pet pet = new Pet("pet-golden-1", "Golden Retriever", new Category("dogs", "Dogs"),
                PetStatus.AVAILABLE, new BigDecimal("499.99"));
        petCatalogModule.addPet(pet);

        Optional<Pet> savedPetOpt = petCatalogModule.findPetById("pet-golden-1");
        assertTrue(savedPetOpt.isPresent());
        assertEquals(PetStatus.AVAILABLE, savedPetOpt.get().getStatus());

        // 2. Place Order
        Order order = new Order();
        order.setId("order-101");
        order.setCustomerId("alice@example.com");
        order.setItems(List.of(new OrderItem("pet-golden-1", 1, new BigDecimal("499.99"))));

        Order placed = petOrderModule.placeOrder(order);
        assertNotNull(placed);
        assertEquals(OrderStatus.PLACED, placed.getStatus());

        // 3. Verify PetStatusSyncInterceptor changed pet status to PENDING
        Pet refreshedPet = petCatalogModule.findPetById("pet-golden-1").orElseThrow();
        assertEquals(PetStatus.PENDING, refreshedPet.getStatus());

        // 4. Verify OrderNotificationInterceptor dispatched notification saved in Memory driver DAL
        List<NotificationLog> logs = notificationLogRepo.findAll();
        assertFalse(logs.isEmpty());
        boolean hasOrderNotification = logs.stream().anyMatch(l -> l.getRecipient().equals("alice@example.com"));
        assertTrue(hasOrderNotification, "Expected notification log for alice@example.com in driver DAL");
    }

    @Test
    @DisplayName("Interceptor Failure: Placing order for unavailable pet throws ValidationException (BEFORE_ADD abort)")
    public void testOrderFailsWhenPetIsUnavailable() {
        // 1. Add pet that is already SOLD
        Pet pet = new Pet("pet-parrot-1", "Macaw Parrot", new Category("birds", "Birds"),
                PetStatus.SOLD, new BigDecimal("899.99"));
        petCatalogModule.addPet(pet);

        // 2. Attempt to place order for sold pet
        Order order = new Order();
        order.setId("order-102");
        order.setCustomerId("bob@example.com");
        order.setItems(List.of(new OrderItem("pet-parrot-1", 1, new BigDecimal("899.99"))));

        // 3. InventoryValidationInterceptor on BEFORE_ADD must abort
        assertThrows(ValidationException.class, () -> {
            petOrderModule.placeOrder(order);
        });

        // 4. Order should not exist
        assertTrue(petOrderModule.findOrderById("order-102").isEmpty());
    }

    @Test
    @DisplayName("Scoped Suppression: InterceptorScope.withoutInterceptors suppresses side-effects")
    public void testScopedSuppressionBypassesInterceptors() {
        Pet pet = new Pet("pet-hamster-1", "Hamster", new Category("rodents", "Rodents"),
                PetStatus.AVAILABLE, new BigDecimal("19.99"));
        petCatalogModule.addPet(pet);

        int logCountBefore = notificationLogRepo.findAll().size();

        // Place order within scoped suppression
        Order order = new Order();
        order.setId("order-bulk-1");
        order.setCustomerId("bulk-importer@petstore.org");
        order.setItems(List.of(new OrderItem("pet-hamster-1", 1, new BigDecimal("19.99"))));

        InterceptorScope.withoutInterceptors(() -> {
            petOrderModule.placeOrder(order);
        });

        // In suppressed scope, Pet status should remain AVAILABLE (sync interceptor skipped)
        Pet currentPet = petCatalogModule.findPetById("pet-hamster-1").orElseThrow();
        assertEquals(PetStatus.AVAILABLE, currentPet.getStatus());

        // And notification should NOT have been generated
        int logCountAfter = notificationLogRepo.findAll().size();
        assertEquals(logCountBefore, logCountAfter);
    }
}
