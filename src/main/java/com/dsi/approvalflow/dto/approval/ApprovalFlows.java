package com.dsi.approvalflow.dto.approval;

import com.dsi.approvalflow.dto.ApprovalFlowSteps;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApprovalFlows {
    private List<ApprovalFlowSteps> flows;

    // TODO: Add additional generic specifications here
}
