package com.dsi.dto;

import java.util.List;

public class ApprovalPath {
    private String applicationType;
    private List<ApprovalPathStep> steps;

    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

    public List<ApprovalPathStep> getSteps() {
        return steps;
    }

    public void setSteps(List<ApprovalPathStep> steps) {
        this.steps = steps;
    }
}
