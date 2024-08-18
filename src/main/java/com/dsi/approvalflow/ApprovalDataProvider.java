package com.dsi.approvalflow;

import com.dsi.approvalflow.dto.ApprovalFlowSteps;
import com.dsi.approvalflow.dto.ApprovalFlowStep;
import com.dsi.approvalflow.dto.ApprovalFlows;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ApprovalDataProvider {
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

    public static List<ApprovalFlowStep> getApprovalFlowSteps(String configPath, String applicationType) {
        ApprovalFlows approvalFlows = getAllApprovalFlowsFromFile(configPath);
        List<ApprovalFlowStep> approvalFlowStepList = new ArrayList<>();
        for (ApprovalFlowSteps approvalFlowSteps : approvalFlows.getFlows()) {
            if (!applicationType.equals(approvalFlowSteps.getApplicationType())) {
                continue;
            }
            approvalFlowStepList = approvalFlowSteps.getSteps();
            break;
        }
        return approvalFlowStepList;
    }
}
