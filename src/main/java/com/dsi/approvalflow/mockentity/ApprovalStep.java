package com.dsi.approvalflow.mockentity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalStep {
    private Long id;
    private ApplicationType applicationType;
    private Role applicantRole;
    private Integer path;
    private Integer level;
    private Role reviewerRole;
    private Boolean startOverOnResubmit;
    private List<AllowedApprovalActions> allowedApprovalActions;
}
