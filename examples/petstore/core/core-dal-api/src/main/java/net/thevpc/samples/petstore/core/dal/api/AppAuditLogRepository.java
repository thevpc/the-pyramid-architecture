package net.thevpc.samples.petstore.core.dal.api;

import net.thevpc.samples.petstore.core.infra.model.AppAuditLog;

import java.util.List;
import java.util.Optional;

public interface AppAuditLogRepository {

    AppAuditLog save(AppAuditLog log);

    Optional<AppAuditLog> findById(String id);

    List<AppAuditLog> findAll();

    List<AppAuditLog> findBySourceModule(String sourceModule);
}
