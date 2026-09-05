package net.thevpc.samples.petstore.core.infra.util;

import net.thevpc.nuts.util.NAssert;
import net.thevpc.samples.petstore.core.infra.exception.ValidationException;

public class AppValidation {

    public static void requireNonNull(Object obj, String message) {
        if (obj == null) {
            throw new ValidationException(message != null ? message : "Object must not be null");
        }
    }

    public static void requireNonBlank(String str, String message) {
        if (str == null || str.trim().isEmpty()) {
            throw new ValidationException(message != null ? message : "String must not be blank");
        }
    }

    public static void requireTrue(boolean condition, String message) {
        if (!condition) {
            throw new ValidationException(message != null ? message : "Condition not met");
        }
    }
}
