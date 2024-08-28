package com.dsi.approvalflow;

import com.dsi.approvalflow.mockentity.ApplicationType;
import com.dsi.approvalflow.mockentity.LeaveApplication;
import com.dsi.approvalflow.service.LeaveApplicationService;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        LeaveApplication application =  createLeaveApplication();
        LeaveApplicationService leaveApplicationService = new LeaveApplicationService(
                ApplicationType.LEAVE_APPLICATION,
                application
        );

        leaveApplicationService.submit();
        leaveApplicationService.sendBack("Documents and info are missing", true, Arrays.asList(Map.of("Application body", "Please attach files")));
        leaveApplicationService.resubmit();
        leaveApplicationService.forward("Seems okay. Inspector suggested.");
        leaveApplicationService.sendBack("Not ready for inspection.", false, null);
        leaveApplicationService.sendBack("Information missing.", true, null);
        leaveApplicationService.resubmit();
        leaveApplicationService.forward("Seems fine");
        leaveApplicationService.forward("Everything seems fine.");
        leaveApplicationService.approve("Approved");
    }

    private static LeaveApplication createLeaveApplication() {
        LeaveApplication application = new LeaveApplication();
        application.setId(1);
        application.setApprovalBody("Please approve.");
        return application;
    }
}