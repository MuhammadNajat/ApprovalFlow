package com.dsi.approvalflow;

import com.dsi.approvalflow.mockentity.Application;
import com.dsi.approvalflow.mockentity.ApplicationType;

import java.util.List;

public interface ApprovalDataProviderBehaviour {
    List<Application> getMyPendings();
    List<Application> getMyPendingsByType(ApplicationType type) ;
    List<Application> getPendingsByUserId(Long userId);
    List<Application> getPendingsByUserIdAndType(Long userId, ApplicationType type) ;
}
