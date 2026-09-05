package net.thevpc.samples.petstore.core.interceptor.api;

public interface EntityInterceptor<T> {
    Class<T> getEntityType();
    LifecyclePhase[] getPhases();
    void onEvent(InterceptorEvent<T> event);

    default int getOrder() {
        return 0;
    }
}
