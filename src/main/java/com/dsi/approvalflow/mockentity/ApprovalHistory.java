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
    private Long applicationId;
    private Long userId;
    private Long employeeId;
    private Long officePostHistoryId;
    private ApprovalActionType actionType;
    private LocalDateTime assignedAt;
    private LocalDateTime reviewedAt;
    private String comment;
    private List<Map<String, String>> correctableFields;
    private List<Long> assignedUsers;

    public void saveAssignment(Long id, Long applicationId, LocalDateTime assignedAt) {
        this.id = id;
        this.applicationId = applicationId;
        this.assignedAt = assignedAt;
    }

    public void saveAssignment(Long applicationId, LocalDateTime assignedAt) {
        this.applicationId = applicationId;
        this.assignedAt = assignedAt;
    }

    public void saveAction(Long userId, Long employeeId, Long officePostHistoryId, LocalDateTime reviewedAt, ApprovalActionType actionType, String comment, List<Map<String, String>> correctableFields, List<Long> assignedUsers) {
        this.userId = userId;
        this.employeeId = employeeId;
        this.officePostHistoryId = officePostHistoryId;
        this.reviewedAt = reviewedAt;
        this.actionType = actionType;
        this.comment = comment;
        this.correctableFields = correctableFields;
        this.assignedUsers = assignedUsers;
    }
}