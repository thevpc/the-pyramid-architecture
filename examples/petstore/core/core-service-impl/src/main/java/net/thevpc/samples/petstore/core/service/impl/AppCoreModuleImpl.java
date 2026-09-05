package net.thevpc.samples.petstore.core.service.impl;

import net.thevpc.samples.petstore.core.actiongraph.ActionExecutionResult;
import net.thevpc.samples.petstore.core.actiongraph.ActionGraph;
import net.thevpc.samples.petstore.core.actiongraph.ActionGraphDispatcher;
import net.thevpc.samples.petstore.core.dal.api.AppAuditLogRepository;
import net.thevpc.samples.petstore.core.infra.model.AppAuditLog;
import net.thevpc.samples.petstore.core.service.api.AppCoreModule;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service("appCoreModule")
@Primary
public class AppCoreModuleImpl implements AppCoreModule {

    private final AppAuditLogRepository auditLogRepository;
    private final ActionGraphDispatcher actionGraphDispatcher;

    public AppCoreModuleImpl(AppAuditLogRepository auditLogRepository, ActionGraphDispatcher actionGraphDispatcher) {
        this.auditLogRepository = auditLogRepository;
        this.actionGraphDispatcher = actionGraphDispatcher;
    }

    @Override
    public AppAuditLog logAudit(String eventType, String sourceModule, String message) {
        String id = "audit-" + UUID.randomUUID().toString().substring(0, 8);
        AppAuditLog log = AppAuditLog.of(id, eventType, sourceModule, message);
        return auditLogRepository.save(log);
    }

    @Override
    public Optional<AppAuditLog> findAuditLogById(String id) {
        return auditLogRepository.findById(id);
    }

    @Override
    public List<AppAuditLog> findAllAuditLogs() {
        return auditLogRepository.findAll();
    }

    @Override
    public List<AppAuditLog> findAuditLogsByModule(String sourceModule) {
        return auditLogRepository.findBySourceModule(sourceModule);
    }

    @Override
    public List<ActionExecutionResult> executeActionGraph(ActionGraph graph) {
        return actionGraphDispatcher.executeGraph(graph);
    }
}
