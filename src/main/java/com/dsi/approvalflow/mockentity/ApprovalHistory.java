package com.dsi.approvalflow.mockentity;

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
    private ApprovalActorType approvalActorType;
    private Long actorId;
    private ApprovalActionType approvalActionType;
    private LocalDateTime dateTime;
    private String comment;
    private List<Map<String, String>> correctableFields;

    public ApprovalHistory(ApprovalActionType approvalActionType, ApprovalActorType approvalActorType, Long actorId, LocalDateTime dateTime, List<Map<String, String>> correctableFields, String comment) {
        this.approvalActionType = approvalActionType;
        this.approvalActorType = approvalActorType;
        this.actorId = actorId;
        this.dateTime = dateTime;
        this.correctableFields = correctableFields;
        this.comment = comment;
    }
}
