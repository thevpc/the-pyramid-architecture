package net.thevpc.samples.petstore.core.dal.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import net.thevpc.samples.petstore.core.infra.model.AppAuditLog;

import java.time.Instant;

@Entity
@Table(name = "core_audit_logs")
public class JpaAppAuditLogEntity {

    @Id
    @Column(length = 64)
    private String id;

    @Column(nullable = false, length = 64)
    private String eventType;

    @Column(nullable = false, length = 64)
    private String sourceModule;

    @Column(nullable = false)
    private Instant timestamp;

    @Column(length = 1024)
    private String message;

    public JpaAppAuditLogEntity() {
    }

    public JpaAppAuditLogEntity(String id, String eventType, String sourceModule, Instant timestamp, String message) {
        this.id = id;
        this.eventType = eventType;
        this.sourceModule = sourceModule;
        this.timestamp = timestamp;
        this.message = message;
    }

    public static JpaAppAuditLogEntity fromModel(AppAuditLog model) {
        return new JpaAppAuditLogEntity(
                model.id(),
                model.eventType(),
                model.sourceModule(),
                model.timestamp(),
                model.message()
        );
    }

    public AppAuditLog toModel() {
        return new AppAuditLog(id, eventType, sourceModule, timestamp, message);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getSourceModule() { return sourceModule; }
    public void setSourceModule(String sourceModule) { this.sourceModule = sourceModule; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
