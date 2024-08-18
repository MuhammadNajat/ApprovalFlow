package com.dsi.approvalflow.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApprovalFlows {
    private List<ApprovalFlowSteps> flows;
}
