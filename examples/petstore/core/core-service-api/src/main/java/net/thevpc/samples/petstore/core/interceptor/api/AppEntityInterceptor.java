package net.thevpc.samples.petstore.core.interceptor.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AppEntityInterceptor {
    Class<?>[] types();
    LifecyclePhase[] phases();
    int order() default 0;
}
