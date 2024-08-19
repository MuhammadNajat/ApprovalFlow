package com.dsi.approvalflow.mockentity;

import com.dsi.approvalflow.dto.ApprovalFlowStep;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApprovalActor {
    private User user;
    private ApprovalFlowStep approvalFlowStep;
}
