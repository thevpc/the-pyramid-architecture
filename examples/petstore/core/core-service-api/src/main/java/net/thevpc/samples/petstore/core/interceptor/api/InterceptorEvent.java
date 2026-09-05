package net.thevpc.samples.petstore.core.interceptor.api;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class InterceptorEvent<T> {
    private final LifecyclePhase phase;
    private final T entity;
    private final Object previousState;
    private final Map<String, Object> properties;

    public InterceptorEvent(LifecyclePhase phase, T entity, Object previousState) {
        this(phase, entity, previousState, new HashMap<>());
    }

    public InterceptorEvent(LifecyclePhase phase, T entity, Object previousState, Map<String, Object> properties) {
        this.phase = phase;
        this.entity = entity;
        this.previousState = previousState;
        this.properties = properties != null ? properties : new HashMap<>();
    }

    public LifecyclePhase getPhase() {
        return phase;
    }

    public T getEntity() {
        return entity;
    }

    public Object getPreviousState() {
        return previousState;
    }

    public Map<String, Object> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    public Object getProperty(String key) {
        return properties.get(key);
    }
}
