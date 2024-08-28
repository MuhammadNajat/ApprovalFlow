package com.dsi.approvalflow.mockentity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Application<T> {
    private T id;
    private Integer path;
    private Integer currentLevel;
    private ApprovalStatus approvalStatus;
    private InternalStatus internalStatus;

    // TODO: determine if they are required
    //private ApprovalActorType applicantType;
    private Long applicantId;
    private String approvalBody;
}
