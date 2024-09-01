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
        
        User applicant = UserService.getUserById(2L);
        User assistantController1 = UserService.getUserById(1L);
        User assistantController2 = UserService.getUserById(5L);
        User controller = UserService.getUserById(3L);
        User secretary = UserService.getUserById(4L);

        // Submitting application
        UserService.setCurrentUser(applicant);
        leaveApplicationService.submit();

        // Sending back for changes to the applicant
        UserService.setCurrentUser(assistantController1);
        leaveApplicationService.sendBack("Documents and info are missing", true,
                Arrays.asList(Map.of("Application body", "Please attach files")));

        // Applicant resubmits the application
        UserService.setCurrentUser(applicant);
        leaveApplicationService.resubmit();

        // Asst Controller suggests Inspector & forwards
        UserService.setCurrentUser(assistantController2);
        leaveApplicationService.forward("Seems okay. Inspector suggested.");

        // Controller founds that application isn't inspection-ready & sends back to Asst Controller
        UserService.setCurrentUser(controller);
        leaveApplicationService.sendBack("Not ready for inspection.", false, null);

        // Asst Controller comments what's missing & sends back to applicant
        UserService.setCurrentUser(assistantController2);
        leaveApplicationService.sendBack("Information missing.", true, null);

        // Applicant resubmits
        UserService.setCurrentUser(applicant);
        leaveApplicationService.resubmit();

        // Asst Controller forwards to Controller
        UserService.setCurrentUser(assistantController1);
        leaveApplicationService.forward("Seems fine");

        // Controller assigns Inspector & forwards to Asst Controller
        UserService.setCurrentUser(controller);
        leaveApplicationService.forward("Inspector assigned. FW when inspection done.");

        // Asst Controller finds Inspection report & payment fine and forwards to Controller
        UserService.setCurrentUser(assistantController1);
        leaveApplicationService.forward("Inspection report & payment fine.");

        // Controller finds everything fine and forwards to Secretary
        UserService.setCurrentUser(controller);
        leaveApplicationService.forward("Everything seems fine.");

        // Secretary approves
        UserService.setCurrentUser(secretary);
        leaveApplicationService.approve("Approved");
    }

    private static LeaveApplication createLeaveApplication() {
        LeaveApplication application = new LeaveApplication();
        application.setId(1L);
        application.setType(ApplicationType.LEAVE_APPLICATION);
        return application;
    }
}