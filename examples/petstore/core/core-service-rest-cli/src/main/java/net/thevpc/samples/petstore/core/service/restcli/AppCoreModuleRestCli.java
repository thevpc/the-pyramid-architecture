package net.thevpc.samples.petstore.core.service.restcli;

import net.thevpc.samples.petstore.core.actiongraph.ActionExecutionResult;
import net.thevpc.samples.petstore.core.actiongraph.ActionGraph;
import net.thevpc.samples.petstore.core.infra.annotation.Generated;
import net.thevpc.samples.petstore.core.infra.model.AppAuditLog;
import net.thevpc.samples.petstore.core.service.api.AppCoreModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service("appCoreModuleRestCli")
@Generated("pyramid")
public class AppCoreModuleRestCli implements AppCoreModule {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    @Autowired
    public AppCoreModuleRestCli(
            @Autowired(required = false) RestTemplate restTemplate,
            @Value("${petstore.modules.core.url:http://localhost:8080/api/core}") String baseUrl) {
        this.restTemplate = restTemplate != null ? restTemplate : new RestTemplate();
        this.baseUrl = baseUrl;
    }

    @Override
    public AppAuditLog logAudit(String eventType, String sourceModule, String message) {
        return restTemplate.postForObject(baseUrl + "/audit-logs", new AuditLogCreateRequest(eventType, sourceModule, message), AppAuditLog.class);
    }

    @Override
    public Optional<AppAuditLog> findAuditLogById(String id) {
        try {
            AppAuditLog log = restTemplate.getForObject(baseUrl + "/audit-logs/" + id, AppAuditLog.class);
            return Optional.ofNullable(log);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<AppAuditLog> findAllAuditLogs() {
        AppAuditLog[] logs = restTemplate.getForObject(baseUrl + "/audit-logs", AppAuditLog[].class);
        return logs != null ? Arrays.asList(logs) : List.of();
    }

    @Override
    public List<AppAuditLog> findAuditLogsByModule(String sourceModule) {
        AppAuditLog[] logs = restTemplate.getForObject(baseUrl + "/audit-logs?module=" + sourceModule, AppAuditLog[].class);
        return logs != null ? Arrays.asList(logs) : List.of();
    }

    @Override
    public List<ActionExecutionResult> executeActionGraph(ActionGraph graph) {
        ActionExecutionResult[] results = restTemplate.postForObject(baseUrl + "/action-graph/execute", graph, ActionExecutionResult[].class);
        return results != null ? Arrays.asList(results) : List.of();
    }

    public record AuditLogCreateRequest(String eventType, String sourceModule, String message) {}
}
