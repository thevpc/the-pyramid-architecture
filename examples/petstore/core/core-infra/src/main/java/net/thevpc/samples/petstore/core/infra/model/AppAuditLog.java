package net.thevpc.samples.petstore.core.infra.model;

import java.time.Instant;

public record AppAuditLog(
        String id,
        String eventType,
        String sourceModule,
        Instant timestamp,
        String message
) {
    public AppAuditLog {
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }

    public static AppAuditLog of(String id, String eventType, String sourceModule, String message) {
        return new AppAuditLog(id, eventType, sourceModule, Instant.now(), message);
    }
}
