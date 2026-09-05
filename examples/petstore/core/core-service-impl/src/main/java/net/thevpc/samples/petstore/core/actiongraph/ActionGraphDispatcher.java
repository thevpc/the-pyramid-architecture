package net.thevpc.samples.petstore.core.actiongraph;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.*;

@Component
public class ActionGraphDispatcher {

    private static final Logger log = LoggerFactory.getLogger(ActionGraphDispatcher.class);
    private final ApplicationContext applicationContext;

    public ActionGraphDispatcher(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public List<ActionExecutionResult> executeGraph(ActionGraph graph) {
        List<ActionExecutionResult> results = new ArrayList<>();
        Map<String, ActionExecutionResult> executedNodes = new HashMap<>();

        for (ActionNode node : graph.getNodes()) {
            if (!node.isApproved()) {
                log.info("Skipping unapproved action node {}", node.getId());
                results.add(ActionExecutionResult.failure(node.getId(), "Node not approved by operator"));
                continue;
            }

            // Check dependencies
            boolean depsMet = true;
            for (String depId : node.getDependsOn()) {
                ActionExecutionResult depRes = executedNodes.get(depId);
                if (depRes == null || !depRes.isSuccess()) {
                    depsMet = false;
                    break;
                }
            }
            if (!depsMet) {
                log.warn("Dependency failure for node {}", node.getId());
                ActionExecutionResult failedDep = ActionExecutionResult.failure(node.getId(), "Preceding dependency failed");
                results.add(failedDep);
                executedNodes.put(node.getId(), failedDep);
                continue;
            }

            try {
                // Find bean matching targetModule
                Object moduleBean = applicationContext.getBean(node.getTargetModule());
                Method matchedMethod = null;
                for (Method m : moduleBean.getClass().getMethods()) {
                    if (m.getName().equalsIgnoreCase(node.getFacadeMethod())) {
                        matchedMethod = m;
                        break;
                    }
                }
                if (matchedMethod == null) {
                    throw new IllegalArgumentException("Method " + node.getFacadeMethod() + " not found on module " + node.getTargetModule());
                }

                // If no parameters or 1 parameter
                Object[] args;
                if (matchedMethod.getParameterCount() == 0) {
                    args = new Object[0];
                } else {
                    // Collect first value from map
                    Object val = node.getParameters() != null && !node.getParameters().isEmpty()
                            ? node.getParameters().values().iterator().next() : null;
                    args = new Object[]{val};
                }

                log.info("Executing AI Action Node [{}]: {}.{}(...)", node.getId(), node.getTargetModule(), node.getFacadeMethod());
                Object output = matchedMethod.invoke(moduleBean, args);
                ActionExecutionResult success = ActionExecutionResult.success(node.getId(), output);
                results.add(success);
                executedNodes.put(node.getId(), success);
            } catch (Exception e) {
                log.error("Execution failed for node {}", node.getId(), e);
                ActionExecutionResult failure = ActionExecutionResult.failure(node.getId(), e.getMessage());
                results.add(failure);
                executedNodes.put(node.getId(), failure);
            }
        }
        return results;
    }
}
