package com.dsi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LeaveApplicationApproval implements Approval {
    static {
        ApprovalDataParser.parseData("/home/najat/Documents/ApprovalFlow/src/main/resources/approvalFlow.json");
    }

    private Logger logger = LogManager.getLogger(LeaveApplicationApproval.class);

    @Override
    public void submit() {
        logger.info("In submit()");
    }

    @Override
    public void sendBack(boolean toApplicant) {
        logger.info("In sendBack()");
    }

    @Override
    public void forward() {
        logger.info("In forward()");
    }

    @Override
    public void approve() {
        logger.info("In approve()");
    }

    @Override
    public void reject() {
        logger.info("In reject()");
    }
}
