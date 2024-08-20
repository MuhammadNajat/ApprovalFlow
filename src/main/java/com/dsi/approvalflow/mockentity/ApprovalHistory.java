package com.dsi.approvalflow.mockentity;

import com.dsi.approvalflow.dto.approval.ApprovalActorType;
import com.dsi.approvalflow.dto.approval.ReviewAction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalHistory {
    private Long id;

    private LocalDateTime assignedOn;
    private ApprovalActorType approvalActorType;
    private Long actorId;

    private LocalDateTime actionTakenOn;
    private ReviewAction actionTaken;
    private String comment;

    // TODO: figure out if this is the best way to request correction
    private List<Map<String, String>> correctableFields;

    public void saveAssignment(LocalDateTime assignedOn, ApprovalActorType approvalActorType, Long actorId) {
        this.assignedOn = assignedOn;
        this.approvalActorType = approvalActorType;
        this.actorId = actorId;
    }

    public void saveActionTaken(LocalDateTime actionTakenOn, ReviewAction actionTaken, String comment) {
        this.actionTakenOn = actionTakenOn;
        this.actionTaken = actionTaken;
        this.comment = comment;
    }
}
