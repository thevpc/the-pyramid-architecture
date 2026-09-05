package net.thevpc.samples.petstore.core.infra.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method or type to be omitted by the code generator for the specified target (e.g. "ws").
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface GeneratorDiscard {
    String value() default "ws";
}
