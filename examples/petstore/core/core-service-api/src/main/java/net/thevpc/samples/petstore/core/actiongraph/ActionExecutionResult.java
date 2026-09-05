package net.thevpc.samples.petstore.core.actiongraph;

public class ActionExecutionResult {
    private final String nodeId;
    private final boolean success;
    private final Object output;
    private final String error;

    public ActionExecutionResult(String nodeId, boolean success, Object output, String error) {
        this.nodeId = nodeId;
        this.success = success;
        this.output = output;
        this.error = error;
    }

    public static ActionExecutionResult success(String nodeId, Object output) {
        return new ActionExecutionResult(nodeId, true, output, null);
    }

    public static ActionExecutionResult failure(String nodeId, String error) {
        return new ActionExecutionResult(nodeId, false, null, error);
    }

    public String getNodeId() {
        return nodeId;
    }

    public boolean isSuccess() {
        return success;
    }

    public Object getOutput() {
        return output;
    }

    public String getError() {
        return error;
    }
}
