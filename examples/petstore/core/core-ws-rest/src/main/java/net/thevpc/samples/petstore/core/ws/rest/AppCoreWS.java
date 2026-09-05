package net.thevpc.samples.petstore.core.ws.rest;

import net.thevpc.samples.petstore.core.actiongraph.ActionExecutionResult;
import net.thevpc.samples.petstore.core.actiongraph.ActionGraph;
import net.thevpc.samples.petstore.core.infra.annotation.AppModuleWS;
import net.thevpc.samples.petstore.core.infra.annotation.Generated;
import net.thevpc.samples.petstore.core.infra.model.AppAuditLog;
import net.thevpc.samples.petstore.core.service.api.AppCoreModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/core")
@AppModuleWS(AppCoreModule.class)
@Generated("pyramid")
public class AppCoreWS {

    @Autowired
    private AppCoreModule appCoreModule;

    @PostMapping("/audit-logs")
    public ResponseEntity<AppAuditLog> logAudit(@RequestBody AuditLogCreateRequest request) {
        return ResponseEntity.ok(appCoreModule.logAudit(request.eventType(), request.sourceModule(), request.message()));
    }

    @GetMapping("/audit-logs/{id}")
    public ResponseEntity<AppAuditLog> findAuditLogById(@PathVariable String id) {
        return appCoreModule.findAuditLogById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<List<AppAuditLog>> findAuditLogs(@RequestParam(required = false) String module) {
        if (module != null && !module.isBlank()) {
            return ResponseEntity.ok(appCoreModule.findAuditLogsByModule(module));
        }
        return ResponseEntity.ok(appCoreModule.findAllAuditLogs());
    }

    @PostMapping("/action-graph/execute")
    public ResponseEntity<List<ActionExecutionResult>> executeActionGraph(@RequestBody ActionGraph graph) {
        return ResponseEntity.ok(appCoreModule.executeActionGraph(graph));
    }

    public record AuditLogCreateRequest(String eventType, String sourceModule, String message) {
    }

    @PostMapping("/logAudit")
    public ResponseEntity<AppAuditLog> logAudit(@RequestParam() String eventType, @RequestParam() String sourceModule, @RequestParam() String message) {
        return ResponseEntity.ok(appCoreModule.logAudit(eventType, sourceModule, message));
    }

    @GetMapping("/findAllAuditLogs")
    public ResponseEntity<List<AppAuditLog>> findAllAuditLogs() {
        return ResponseEntity.ok(appCoreModule.findAllAuditLogs());
    }

    @GetMapping("/findAuditLogsByModule")
    public ResponseEntity<List<AppAuditLog>> findAuditLogsByModule(@RequestParam() String sourceModule) {
        return ResponseEntity.ok(appCoreModule.findAuditLogsByModule(sourceModule));
    }
}
