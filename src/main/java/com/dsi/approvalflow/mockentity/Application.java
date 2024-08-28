package com.dsi.approvalflow.mockentity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Application {
    private Long id;
    private ApplicationType type;
    private Integer pathNo;
    private Integer currentStepNo;
    private ApprovalStatus approvalStatus;
    private InternalStatus internalStatus;
}
