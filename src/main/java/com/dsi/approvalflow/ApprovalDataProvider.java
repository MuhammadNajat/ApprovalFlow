package com.dsi.approvalflow;

import com.dsi.approvalflow.dto.ApprovalPath;
import com.dsi.approvalflow.dto.ApprovalPathStep;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class ApprovalDataProvider {
    private static Logger logger = LogManager.getLogger(ApprovalDataProvider.class);
    private static List<ApprovalPath> approvalPaths = ApprovalDataParser.getApprovalPaths().getPaths();

    public static List<ApprovalPathStep> getApprovalPathSteps(String applicationType) {
        List<ApprovalPathStep> approvalPathSteps = new ArrayList<>();
        for(ApprovalPath approvalPath : approvalPaths) {
            if(!applicationType.equals(approvalPath.getApplicationType())) {
                logger.info("AppType Mismatched: {}", approvalPath.getApplicationType());
                continue;
            }
            logger.info("Application Type found: {}", approvalPath.getApplicationType());
            approvalPathSteps = approvalPath.getSteps();
            break;
        }
        return  approvalPathSteps;
    }
}
