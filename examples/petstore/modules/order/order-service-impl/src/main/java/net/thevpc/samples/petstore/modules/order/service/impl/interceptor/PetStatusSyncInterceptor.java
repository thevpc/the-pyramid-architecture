package net.thevpc.samples.petstore.modules.order.service.impl.interceptor;

import net.thevpc.samples.petstore.core.interceptor.api.AppEntityInterceptor;
import net.thevpc.samples.petstore.core.interceptor.api.EntityInterceptor;
import net.thevpc.samples.petstore.core.interceptor.api.InterceptorEvent;
import net.thevpc.samples.petstore.core.interceptor.api.LifecyclePhase;
import net.thevpc.samples.petstore.modules.catalog.infra.PetStatus;
import net.thevpc.samples.petstore.modules.catalog.service.api.PetCatalogModule;
import net.thevpc.samples.petstore.modules.order.infra.Order;
import net.thevpc.samples.petstore.modules.order.infra.OrderItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Cross-module side effect interceptor.
 * Executes on AFTER_ADD of Order.
 * Calls PetCatalogModule facade (apex-to-apex) to mark ordered pets as PENDING.
 */
@Component
@AppEntityInterceptor(types = Order.class, phases = LifecyclePhase.AFTER_ADD)
public class PetStatusSyncInterceptor implements EntityInterceptor<Order> {

    private static final Logger log = LoggerFactory.getLogger(PetStatusSyncInterceptor.class);
    private final PetCatalogModule petCatalogModule;

    @Autowired
    public PetStatusSyncInterceptor(PetCatalogModule petCatalogModule) {
        this.petCatalogModule = petCatalogModule;
    }

    @Override
    public Class<Order> getEntityType() {
        return Order.class;
    }

    @Override
    public LifecyclePhase[] getPhases() {
        return new LifecyclePhase[]{LifecyclePhase.AFTER_ADD};
    }

    @Override
    public void onEvent(InterceptorEvent<Order> event) {
        Order order = event.getEntity();
        log.info("PetStatusSyncInterceptor: Synchronizing pet statuses for order {}", order.getId());

        for (OrderItem item : order.getItems()) {
            petCatalogModule.updatePetStatus(item.getPetId(), PetStatus.PENDING);
            log.info("PetStatusSyncInterceptor: Updated pet {} status to PENDING", item.getPetId());
        }
    }
}
