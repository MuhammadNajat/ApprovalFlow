package com.dsi.approvalflow.service;

import com.dsi.approvalflow.mockentity.ApplicationType;
import com.dsi.approvalflow.mockentity.LeaveApplication;
import lombok.Getter;

@Getter     // not required for actual usage
public class LeaveApplicationService extends LeaveApplicationApprovalFlow {

    private LeaveApplication application;

    public LeaveApplicationService(ApplicationType applicationType, LeaveApplication application) {
        super(applicationType, application);
        this.application = application;
    }
}
