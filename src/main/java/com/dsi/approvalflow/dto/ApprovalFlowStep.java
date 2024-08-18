package com.dsi.approvalflow.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApprovalFlowStep {
    private Approver approver;
    private List<String> allowedActions;
}
