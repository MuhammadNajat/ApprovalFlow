package com.dsi.approvalflow.service;

import com.dsi.approvalflow.mockentity.LeaveApplication;
import lombok.Getter;

@Getter     // not required for actual usage
public class LeaveApplicationService extends LeaveApplicationApprovalFlow {

    private LeaveApplication application;

    public LeaveApplicationService(LeaveApplication application) {
        super(application);
        this.application = application;
    }
}
