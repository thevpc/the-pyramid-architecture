package net.thevpc.samples.petstore.core.interceptor.impl;

import net.thevpc.samples.petstore.core.interceptor.api.EntityInterceptor;
import net.thevpc.samples.petstore.core.interceptor.api.LifecyclePhase;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class InterceptorRegistry {

    private final Map<Class<?>, List<EntityInterceptor<?>>> interceptorsByEntity = new ConcurrentHashMap<>();
    private final ObjectProvider<List<EntityInterceptor<?>>> interceptorProvider;
    private volatile boolean initialized = false;

    public InterceptorRegistry(ObjectProvider<List<EntityInterceptor<?>>> interceptorProvider) {
        this.interceptorProvider = interceptorProvider;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void onContextRefreshed() {
        ensureInitialized();
    }

    private void ensureInitialized() {
        if (!initialized) {
            synchronized (this) {
                if (!initialized) {
                    if (interceptorProvider != null) {
                        List<EntityInterceptor<?>> interceptors = interceptorProvider.getIfAvailable();
                        if (interceptors != null) {
                            for (EntityInterceptor<?> interceptor : interceptors) {
                                register(interceptor);
                            }
                        }
                    }
                    initialized = true;
                }
            }
        }
    }

    public synchronized void register(EntityInterceptor<?> interceptor) {
        Class<?> entityType = interceptor.getEntityType();
        interceptorsByEntity.computeIfAbsent(entityType, k -> new CopyOnWriteArrayList<>()).add(interceptor);
        interceptorsByEntity.get(entityType).sort(Comparator.comparingInt(EntityInterceptor::getOrder));
    }

    @SuppressWarnings("unchecked")
    public <T> List<EntityInterceptor<T>> getInterceptors(Class<T> entityType, LifecyclePhase phase) {
        ensureInitialized();
        List<EntityInterceptor<?>> list = interceptorsByEntity.get(entityType);
        if (list == null) {
            return Collections.emptyList();
        }
        List<EntityInterceptor<T>> matched = new ArrayList<>();
        for (EntityInterceptor<?> interceptor : list) {
            for (LifecyclePhase p : interceptor.getPhases()) {
                if (p == phase) {
                    matched.add((EntityInterceptor<T>) interceptor);
                    break;
                }
            }
        }
        return matched;
    }
}
