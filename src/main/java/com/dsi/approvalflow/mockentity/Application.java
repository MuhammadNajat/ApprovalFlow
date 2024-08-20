package com.dsi.approvalflow.mockentity;

import com.dsi.approvalflow.dto.approval.ApprovalActorType;
import com.dsi.approvalflow.dto.approval.ApprovalState;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Application<T> {
    private T id;
    private ApprovalState status;
    private ApprovalActorType currentReviewerType;
    private Long currentReviewerId;

    // TODO: determine if they are required
    private ApprovalActorType applicantType;
    private Long applicantId;
    private String approvalBody;
}
