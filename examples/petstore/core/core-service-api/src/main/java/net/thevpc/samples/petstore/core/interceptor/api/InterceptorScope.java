package net.thevpc.samples.petstore.core.interceptor.api;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Scoped context mechanism for deliberately suppressing interceptors
 * (e.g. during bulk data migration or batch processing).
 */
public class InterceptorScope {

    private static final ThreadLocal<Boolean> ALL_SUPPRESSED = ThreadLocal.withInitial(() -> false);
    private static final ThreadLocal<Set<Class<?>>> SUPPRESSED_INTERCEPTORS = ThreadLocal.withInitial(HashSet::new);

    public static void withoutInterceptors(Runnable action) {
        withoutInterceptors(() -> {
            action.run();
            return null;
        });
    }

    public static <R> R withoutInterceptors(Supplier<R> action) {
        boolean previous = ALL_SUPPRESSED.get();
        try {
            ALL_SUPPRESSED.set(true);
            return action.get();
        } finally {
            ALL_SUPPRESSED.set(previous);
        }
    }

    public static void without(Class<?> interceptorClass, Runnable action) {
        without(Collections.singleton(interceptorClass), () -> {
            action.run();
            return null;
        });
    }

    public static <R> R without(Set<Class<?>> interceptorClasses, Supplier<R> action) {
        Set<Class<?>> current = SUPPRESSED_INTERCEPTORS.get();
        Set<Class<?>> updated = new HashSet<>(current);
        updated.addAll(interceptorClasses);
        try {
            SUPPRESSED_INTERCEPTORS.set(updated);
            return action.get();
        } finally {
            SUPPRESSED_INTERCEPTORS.set(current);
        }
    }

    public static boolean isSuppressed(Class<?> interceptorClass) {
        if (Boolean.TRUE.equals(ALL_SUPPRESSED.get())) {
            return true;
        }
        return SUPPRESSED_INTERCEPTORS.get().contains(interceptorClass);
    }
}
