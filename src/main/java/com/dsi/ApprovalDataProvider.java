package com.dsi;

import com.dsi.dto.ApprovalPath;
import com.dsi.dto.ApprovalPathStep;
import com.dsi.dto.ApprovalPaths;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;
import org.apache.logging.log4j.message.StringFormattedMessage;

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
