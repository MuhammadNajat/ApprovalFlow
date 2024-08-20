package com.dsi.approvalflow.dto;

import com.dsi.approvalflow.dto.approval.ApprovalFlowStep;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApprovalFlowSteps {
    private ApplicationType applicationType;
    private List<ApprovalFlowStep> steps;
}
