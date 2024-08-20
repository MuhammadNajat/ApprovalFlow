package com.dsi.approvalflow.dto;

import com.dsi.approvalflow.dto.approval.ReviewerType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Approver {
    private ReviewerType type;
    private Integer postTypeId;             // i.e. Controller, Asst. Controller
    private Long officeLayerId;             // i.e. Administration, IT, Accounts
    private String role;                    // i.e. TEAM_LEAD, MANAGER
}
