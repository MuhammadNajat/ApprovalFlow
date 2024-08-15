package com.dsi;

import com.dsi.dto.ApprovalPathStep;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;

public class Main {
    private static Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        ApprovalDataParser approvalDataParser = new ApprovalDataParser();
        //approvalDataParser.parseData("/home/najat/Documents/ApprovalFlow/src/main/resources/approvalFlow.json");
        //logger.info("Parsing done");

        LeaveApplicationApproval leaveApplicationApproval = new LeaveApplicationApproval();
        leaveApplicationApproval.submit();
        leaveApplicationApproval.forward();
        leaveApplicationApproval.approve();
        leaveApplicationApproval.sendBack(true);
        leaveApplicationApproval.reject();

        List<ApprovalPathStep> steps = ApprovalDataProvider.getApprovalPathSteps("LEAVE_APPLICATION");
        for(ApprovalPathStep step : steps) {
            logger.info("Approval Path Steps. Type: {}. Allowed Actions {}.", step.getApprover().getType(), step.getAllowedActions());
        }
    }
}