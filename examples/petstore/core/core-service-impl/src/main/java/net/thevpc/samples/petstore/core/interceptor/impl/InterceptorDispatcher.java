package net.thevpc.samples.petstore.core.interceptor.impl;

import net.thevpc.samples.petstore.core.interceptor.api.EntityInterceptor;
import net.thevpc.samples.petstore.core.interceptor.api.InterceptorEvent;
import net.thevpc.samples.petstore.core.interceptor.api.InterceptorScope;
import net.thevpc.samples.petstore.core.interceptor.api.LifecyclePhase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InterceptorDispatcher {

    private static final Logger log = LoggerFactory.getLogger(InterceptorDispatcher.class);

    private final InterceptorRegistry registry;

    public InterceptorDispatcher(InterceptorRegistry registry) {
        this.registry = registry;
    }

    @SuppressWarnings("unchecked")
    public <T> void dispatch(LifecyclePhase phase, T entity, Object previousState) {
        if (entity == null) {
            return;
        }
        Class<T> entityType = (Class<T>) entity.getClass();
        List<EntityInterceptor<T>> interceptors = registry.getInterceptors(entityType, phase);
        if (interceptors.isEmpty()) {
            return;
        }

        InterceptorEvent<T> event = new InterceptorEvent<>(phase, entity, previousState);
        for (EntityInterceptor<T> interceptor : interceptors) {
            if (InterceptorScope.isSuppressed(interceptor.getClass())) {
                log.debug("Interceptor {} is suppressed by current scope", interceptor.getClass().getSimpleName());
                continue;
            }

            try {
                interceptor.onEvent(event);
            } catch (RuntimeException e) {
                if (phase.name().startsWith("BEFORE_")) {
                    log.error("BEFORE phase interceptor {} failed, aborting operation: {}",
                            interceptor.getClass().getSimpleName(), e.getMessage());
                    throw e;
                } else {
                    log.error("AFTER phase interceptor {} failed: {}",
                            interceptor.getClass().getSimpleName(), e.getMessage(), e);
                    // Asymmetric failure handling: after phase has already committed, so log/handle
                    throw e;
                }
            }
        }
    }
}
