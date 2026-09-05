package net.thevpc.samples.petstore.core.infra.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares the Module Facade interface exposed by this WS controller.
 * Used by the code generator to synchronize endpoints losslessly.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AppModuleWS {
    Class<?> value();
}
