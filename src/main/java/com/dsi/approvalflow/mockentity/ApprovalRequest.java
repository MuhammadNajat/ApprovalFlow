package com.dsi.approvalflow.mockentity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApprovalRequest {
    private Long id;
    private ApprovalActorType applicantType;
    private Long applicantId;
    private String approvalBody;
    private ApprovalState state;
    private ApprovalActorType currentReviewerType;
    private Long currentReviewerId;
}
