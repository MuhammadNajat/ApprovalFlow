package com.dsi.approvalflow;

import com.dsi.approvalflow.dto.ApprovalPath;
import com.dsi.approvalflow.dto.ApprovalPathStep;
import com.dsi.approvalflow.dto.ApprovalPaths;
import com.dsi.approvalflow.dto.Approver;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class ApprovalDataParser {
    private static Logger logger = LogManager.getLogger(ApprovalDataParser.class);
    static ApprovalPaths approvalPaths;

    public static void parseData(String configPath) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            approvalPaths = objectMapper.readValue(new File(configPath), ApprovalPaths.class);

            for (ApprovalPath approvalPath : approvalPaths.getPaths()) {
                logger.info("Application Type: {}", approvalPath.getApplicationType());
                for (ApprovalPathStep step : approvalPath.getSteps()) {
                    Approver approver = step.getApprover();
                    logger.info("Approver Type: {}", approver.getType());
                    if ("POST".equals(approver.getType())) {
                        logger.info("Post ID: {}", approver.getPostId());
                    } else if ("ROLE".equals(approver.getType())) {
                        logger.info("Office Layer ID: {}", approver.getOfficeLayerId());
                        logger.info("Role: {}", approver.getRole());
                    }
                    logger.info("Allowed Actions: {}", step.getAllowedActions());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ApprovalPaths getApprovalPaths() {
        return approvalPaths;
    }
}
