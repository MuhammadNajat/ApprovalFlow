package com.dsi.approvalflow;

import com.dsi.approvalflow.dto.ApprovalPath;
import com.dsi.approvalflow.dto.ApprovalPathStep;

import java.util.ArrayList;
import java.util.List;

public class ApprovalDataProvider {
    private static List<ApprovalPath> approvalPaths = ApprovalDataParser.getApprovalPaths().getPaths();

    public static List<ApprovalPathStep> getApprovalPathSteps(String applicationType) {
        List<ApprovalPathStep> approvalPathSteps = new ArrayList<>();
        for(ApprovalPath approvalPath : approvalPaths) {
            if(!applicationType.equals(approvalPath.getApplicationType())) {
                continue;
            }
            approvalPathSteps = approvalPath.getSteps();
            break;
        }
        return  approvalPathSteps;
    }
}
