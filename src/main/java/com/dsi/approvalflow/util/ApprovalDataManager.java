package com.dsi.approvalflow.util;

import com.dsi.approvalflow.dto.ApplicationType;
import com.dsi.approvalflow.dto.ApprovalFlowSteps;
import com.dsi.approvalflow.dto.approval.ApprovalFlowStep;
import com.dsi.approvalflow.dto.approval.ApprovalFlows;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ApprovalDataManager {
    private static ApprovalFlows getAllApprovalFlowsFromFile(String configPath) {
        ObjectMapper objectMapper = new ObjectMapper();
        ApprovalFlows approvalFlows = new ApprovalFlows();

        try {
            approvalFlows = objectMapper.readValue(new File(configPath), ApprovalFlows.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return approvalFlows;
    }

    public static List<ApprovalFlowStep> getApprovalFlowSteps(String configPath, ApplicationType applicationType) {
        ApprovalFlows approvalFlows = getAllApprovalFlowsFromFile(Objects.requireNonNull(ApprovalDataManager.class
                .getClassLoader().getResource(configPath)).getPath());
        List<ApprovalFlowStep> approvalFlowStepList = new ArrayList<>();
        for (ApprovalFlowSteps approvalFlowSteps : approvalFlows.getFlows()) {
            if (!applicationType.name().equals(approvalFlowSteps.getApplicationType())) {
                continue;
            }
            approvalFlowStepList = approvalFlowSteps.getSteps();
            break;
        }
        return approvalFlowStepList;
    }
}
