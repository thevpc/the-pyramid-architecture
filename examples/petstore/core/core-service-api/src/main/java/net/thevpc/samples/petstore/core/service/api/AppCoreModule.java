package net.thevpc.samples.petstore.core.service.api;

import net.thevpc.samples.petstore.core.actiongraph.ActionExecutionResult;
import net.thevpc.samples.petstore.core.actiongraph.ActionGraph;
import net.thevpc.samples.petstore.core.infra.model.AppAuditLog;

import java.util.List;
import java.util.Optional;

public interface AppCoreModule {

    AppAuditLog logAudit(String eventType, String sourceModule, String message);

    Optional<AppAuditLog> findAuditLogById(String id);

    List<AppAuditLog> findAllAuditLogs();

    List<AppAuditLog> findAuditLogsByModule(String sourceModule);

    List<ActionExecutionResult> executeActionGraph(ActionGraph graph);
}
