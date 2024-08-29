package com.dsi.approvalflow;

import com.dsi.approvalflow.mockentity.ApplicationType;
import com.dsi.approvalflow.mockentity.LeaveApplication;
import com.dsi.approvalflow.mockentity.User;
import com.dsi.approvalflow.service.LeaveApplicationService;
import com.dsi.approvalflow.service.UserService;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        LeaveApplication application = createLeaveApplication();
        LeaveApplicationService leaveApplicationService = new LeaveApplicationService(application);

        UserService userService = new UserService();

        User applicant = userService.getUserById(2L);
        User assistantController1 = userService.getUserById(1L);
        User assistantController2 = userService.getUserById(5L);
        User controller = userService.getUserById(3L);
        User secretary = userService.getUserById(4L);

        // Submitting application
        userService.setCurrentUser(applicant);
        leaveApplicationService.submit();

        // Sending back for changes to the applicant
        userService.setCurrentUser(assistantController1);
        leaveApplicationService.sendBack("Documents and info are missing", true,
                Arrays.asList(Map.of("Application body", "Please attach files")));

        userService.setCurrentUser(applicant);
        leaveApplicationService.resubmit();

        userService.setCurrentUser(assistantController2);
        leaveApplicationService.forward("Seems okay. Inspector suggested.");

        userService.setCurrentUser(controller);
        leaveApplicationService.sendBack("Not ready for inspection.", false, null);

        userService.setCurrentUser(assistantController2);
        leaveApplicationService.sendBack("Information missing.", true, null);

        userService.setCurrentUser(applicant);
        leaveApplicationService.resubmit();

        userService.setCurrentUser(assistantController1);
        leaveApplicationService.forward("Seems fine");

        userService.setCurrentUser(controller);
        leaveApplicationService.forward("Inspector assigned. FW when inspection done.");

        userService.setCurrentUser(assistantController1);
        leaveApplicationService.forward("Inspection report & payment fine.");

        userService.setCurrentUser(controller);
        leaveApplicationService.forward("Everything seems fine.");

        userService.setCurrentUser(secretary);
        leaveApplicationService.approve("Approved");
    }

    private static LeaveApplication createLeaveApplication() {
        LeaveApplication application = new LeaveApplication();
        application.setId(1L);
        application.setType(ApplicationType.LEAVE_APPLICATION);
        return application;
    }
}