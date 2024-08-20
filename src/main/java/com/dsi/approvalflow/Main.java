package com.dsi.approvalflow;

import com.dsi.approvalflow.dto.ApplicationType;
import com.dsi.approvalflow.dto.approval.ApprovalState;
import com.dsi.approvalflow.mockentity.LeaveApplication;
import com.dsi.approvalflow.service.LeaveApplicationService;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        LeaveApplication application =  createLeaveApplication();
        LeaveApplicationService leaveApplicationService = new LeaveApplicationService(
                ApplicationType.LEAVE_APPLICATION,
                application
        );

        leaveApplicationService.submit();
        System.out.println("Current status of the application: " + leaveApplicationService.getApplication().getStatus());

        leaveApplicationService.forward("Okay for me");
        System.out.println("Current status of the application: " + leaveApplicationService.getApplication().getStatus());

        leaveApplicationService.sendBack("Check carefully", false);
        System.out.println("Current status of the application: " + leaveApplicationService.getApplication().getStatus());

        leaveApplicationService.sendBack("Check carefully 2", false);
        System.out.println("Current status of the application: " + leaveApplicationService.getApplication().getStatus());

        leaveApplicationService.sendBack("Check carefully 3", false);
        System.out.println("Current status of the application: " + leaveApplicationService.getApplication().getStatus());

        leaveApplicationService.sendBack("Check carefully 4", false);
        System.out.println("Current status of the application: " + leaveApplicationService.getApplication().getStatus());

        leaveApplicationService.forward("Seems good");
        System.out.println("Current status of the application: " + leaveApplicationService.getApplication().getStatus());

        leaveApplicationService.sendBackForCorrection("Sending back", true, null);
        System.out.println("Current status of the application: " + leaveApplicationService.getApplication().getStatus());

        leaveApplicationService.forward("Corrected.");
        System.out.println("Current status of the application: " + leaveApplicationService.getApplication().getStatus());

        leaveApplicationService.forward("Checked. 1");
        System.out.println("Current status of the application: " + leaveApplicationService.getApplication().getStatus());

        leaveApplicationService.forward("Checked. 2");
        System.out.println("Current status of the application: " + leaveApplicationService.getApplication().getStatus());

        leaveApplicationService.forward("Checked. 3");
        System.out.println("Current status of the application: " + leaveApplicationService.getApplication().getStatus());

        leaveApplicationService.approve("Approved. Thanks.");
        System.out.println("Current status of the application: " + leaveApplicationService.getApplication().getStatus());
    }

    private static LeaveApplication createLeaveApplication() {
        LeaveApplication application = new LeaveApplication();
        application.setId(1);
        application.setFromDate(LocalDate.now());
        application.setTillDate(LocalDate.now());
        application.setReason("Sick Leave");
        application.setEmployeeId(1);
        application.setStatus(ApprovalState.DRAFT);
        return application;
    }
}