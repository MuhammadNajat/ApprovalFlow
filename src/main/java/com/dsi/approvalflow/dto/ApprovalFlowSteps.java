package com.dsi.approvalflow.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApprovalFlowSteps {
    private String applicationType;
    private List<ApprovalFlowStep> steps;
}
