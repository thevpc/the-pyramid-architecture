package net.thevpc.samples.petstore.core.actiongraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Node in an AI-proposed Action Graph representing a single typed facade call.
 */
public class ActionNode {
    private String id;
    private String targetModule;
    private String facadeMethod;
    private Map<String, Object> parameters;
    private String reasoning;
    private double confidence;
    private List<String> dependsOn = new ArrayList<>();
    private boolean approved;

    public ActionNode() {
    }

    public ActionNode(String id, String targetModule, String facadeMethod, Map<String, Object> parameters,
                      String reasoning, double confidence, List<String> dependsOn) {
        this.id = id;
        this.targetModule = targetModule;
        this.facadeMethod = facadeMethod;
        this.parameters = parameters;
        this.reasoning = reasoning;
        this.confidence = confidence;
        if (dependsOn != null) {
            this.dependsOn = dependsOn;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTargetModule() {
        return targetModule;
    }

    public void setTargetModule(String targetModule) {
        this.targetModule = targetModule;
    }

    public String getFacadeMethod() {
        return facadeMethod;
    }

    public void setFacadeMethod(String facadeMethod) {
        this.facadeMethod = facadeMethod;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public String getReasoning() {
        return reasoning;
    }

    public void setReasoning(String reasoning) {
        this.reasoning = reasoning;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public List<String> getDependsOn() {
        return dependsOn;
    }

    public void setDependsOn(List<String> dependsOn) {
        this.dependsOn = dependsOn;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}
