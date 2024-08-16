package com.dsi.approvalflow.dto;

import java.util.List;

public class ApprovalPathStep {
    private Approver approver;
    private List<String> allowedActions;

    public Approver getApprover() {
        return approver;
    }

    public void setApprover(Approver approver) {
        this.approver = approver;
    }

    public List<String> getAllowedActions() {
        return allowedActions;
    }

    public void setAllowedActions(List<String> allowedActions) {
        this.allowedActions = allowedActions;
    }
}
