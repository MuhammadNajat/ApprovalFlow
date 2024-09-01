package com.dsi.approvalflow;

import com.dsi.approvalflow.mockentity.Application;
import com.dsi.approvalflow.mockentity.ApplicationType;

import java.util.ArrayList;
import java.util.List;

public class ApprovalDataProvider implements ApprovalDataProviderBehaviour {
    public List<Application> getMyPendings() {
        List<Application> applications = new ArrayList<>();
        return applications;
    }
    public List<Application> getMyPendingsByType(ApplicationType type) {
        List<Application> applications = new ArrayList<>();
        return applications;
    }
    public List<Application> getPendingsByUserId(Long userId) {
        List<Application> applications = new ArrayList<>();
        return applications;
    }
    public List<Application> getPendingsByUserIdAndType(Long userId, ApplicationType type) {
        List<Application> applications = new ArrayList<>();
        return applications;
    }
}
