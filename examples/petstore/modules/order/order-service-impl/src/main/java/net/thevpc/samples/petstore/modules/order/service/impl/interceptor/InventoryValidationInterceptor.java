package net.thevpc.samples.petstore.modules.order.service.impl.interceptor;

import net.thevpc.samples.petstore.core.infra.exception.ValidationException;
import net.thevpc.samples.petstore.core.interceptor.api.AppEntityInterceptor;
import net.thevpc.samples.petstore.core.interceptor.api.EntityInterceptor;
import net.thevpc.samples.petstore.core.interceptor.api.InterceptorEvent;
import net.thevpc.samples.petstore.core.interceptor.api.LifecyclePhase;
import net.thevpc.samples.petstore.modules.catalog.infra.Pet;
import net.thevpc.samples.petstore.modules.catalog.infra.PetStatus;
import net.thevpc.samples.petstore.modules.catalog.service.api.PetCatalogModule;
import net.thevpc.samples.petstore.modules.order.infra.Order;
import net.thevpc.samples.petstore.modules.order.infra.OrderItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Cross-module validation interceptor.
 * Executes on BEFORE_ADD of Order.
 * Calls PetCatalogModule facade (apex-to-apex) to ensure requested pets are AVAILABLE.
 * If not, throws ValidationException, aborting the order placement before commit.
 */
@Component
@AppEntityInterceptor(types = Order.class, phases = LifecyclePhase.BEFORE_ADD)
public class InventoryValidationInterceptor implements EntityInterceptor<Order> {

    private static final Logger log = LoggerFactory.getLogger(InventoryValidationInterceptor.class);
    private final PetCatalogModule petCatalogModule;

    @Autowired
    public InventoryValidationInterceptor(PetCatalogModule petCatalogModule) {
        this.petCatalogModule = petCatalogModule;
    }

    @Override
    public Class<Order> getEntityType() {
        return Order.class;
    }

    @Override
    public LifecyclePhase[] getPhases() {
        return new LifecyclePhase[]{LifecyclePhase.BEFORE_ADD};
    }

    @Override
    public void onEvent(InterceptorEvent<Order> event) {
        Order order = event.getEntity();
        log.info("InventoryValidationInterceptor validating order {} with {} items",
                order.getId(), order.getItems().size());

        for (OrderItem item : order.getItems()) {
            Optional<Pet> opt = petCatalogModule.findPetById(item.getPetId());
            if (opt.isEmpty()) {
                throw new ValidationException("Cannot place order: Pet not found with ID " + item.getPetId());
            }
            Pet pet = opt.get();
            if (pet.getStatus() != PetStatus.AVAILABLE) {
                throw new ValidationException(String.format(
                        "Cannot place order: Pet '%s' (ID: %s) is currently %s",
                        pet.getName(), pet.getId(), pet.getStatus()
                ));
            }
        }
        log.info("InventoryValidationInterceptor: All items valid and available for order {}", order.getId());
    }
}
