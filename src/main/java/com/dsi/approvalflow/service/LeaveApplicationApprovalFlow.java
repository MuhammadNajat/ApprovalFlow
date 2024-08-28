package com.dsi.approvalflow.service;

import com.dsi.approvalflow.BaseApprovalFlow;
import com.dsi.approvalflow.mockentity.ApplicationType;
import com.dsi.approvalflow.mockentity.Application;

public class LeaveApplicationApprovalFlow extends BaseApprovalFlow {
    public LeaveApplicationApprovalFlow(ApplicationType applicationType, Application<?> application) {
        super(applicationType, application);
    }
}
