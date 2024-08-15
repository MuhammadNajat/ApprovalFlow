package org.example;

import java.util.List;

class ApprovalStep {
    private int approverPostId;
    private List<String> allowedActions;

    // Getters and Setters
    public int getApproverPostId() {
        return approverPostId;
    }

    public void setApproverPostId(int approverPostId) {
        this.approverPostId = approverPostId;
    }

    public List<String> getAllowedActions() {
        return allowedActions;
    }

    public void setAllowedActions(List<String> allowedActions) {
        this.allowedActions = allowedActions;
    }
}