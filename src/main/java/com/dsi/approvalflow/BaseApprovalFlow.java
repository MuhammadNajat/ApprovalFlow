package com.dsi.approvalflow;

import com.dsi.approvalflow.mockentity.ApplicationType;
import com.dsi.approvalflow.mockentity.*;
import com.dsi.approvalflow.service.ApplicationRoleService;
import com.dsi.approvalflow.service.ApprovalStepService;

import java.time.LocalDateTime;
import java.util.*;

;

public class BaseApprovalFlow implements ApprovalFlow {
    private Application application;
    private List<ApprovalStep> approvalSteps;
    private List<ApprovalHistory> histories = new ArrayList<>();
    private ApplicationType applicationType;


    private Map<Long, List<Role>> applicationToRole = new HashMap<>();

    //Dummy services
    private ApprovalStepService approvalStepService = new ApprovalStepService();
    private ApplicationRoleService applicationRoleService = new ApplicationRoleService();

    public BaseApprovalFlow(Application application) {
        this.applicationType = application.getType();
        //approvalSteps = approvalStepService.getApprovalSteps(ApplicationType.LEAVE_APPLICATION, "EMPLOYEE");
        this.application = application;
    }

    @Override
    public void submit() {
        // TODO: Add validation
        List<ApprovalStep> initialSteps = approvalStepService.getInitialStep(ApplicationType.LEAVE_APPLICATION, new ArrayList<>());
        // TODO: Handle exceptions

        application.setApprovalStatus(ApprovalStatus.PENDING);
        application.setInternalStatus(InternalStatus.SUBMITTED);

        application.setPathNo(initialSteps.get(0).getPathNo());
        application.setCurrentStepNo(initialSteps.get(0).getStepNo());


        // TODO: use stream/map
        List<Role> roles = getRoles(initialSteps);
        applicationRoleService.setRolesForApplication(application.getId(), roles);


        ApprovalHistory history = new ApprovalHistory();
        history.saveAssignment(Long.valueOf(histories.size()), application.getId(), LocalDateTime.now());

        histories.add(history);

        // TODO: async / aspect & integrate with notification service
        notifyUsers(roles);

        //Debug code
        System.out.println("*** Showing history on submit():");
        showHistory();
    }

    @Override
    public void resubmit() {
        ApprovalHistory resubmitHistory = histories.get(histories.size() - 1);
        resubmitHistory.saveAction(1L, 1L, 1L, LocalDateTime.now(), ApprovalActionType.RESUBMIT, null, null, Arrays.asList(1L, 4L));

        application.setApprovalStatus(ApprovalStatus.PENDING);

        List<ApprovalStep> currentSteps = approvalStepService.getCurrentSteps(ApplicationType.LEAVE_APPLICATION, new ArrayList<>(), application.getPathNo(), application.getCurrentStepNo());

        List<ApprovalStep> nextSteps = approvalStepService.getCurrentSteps(ApplicationType.LEAVE_APPLICATION, new ArrayList<>(), application.getPathNo(), application.getCurrentStepNo());;
        if(currentSteps.get(0).getStartOverOnResubmit()) {
            nextSteps = approvalStepService.getNextStep(ApplicationType.LEAVE_APPLICATION, null, null);
        }

        application.setPathNo(nextSteps.get(0).getPathNo());
        application.setCurrentStepNo(nextSteps.get(0).getStepNo());

        List<Role> roles = getRoles(nextSteps);
        applicationRoleService.removeRolesForApplication(Long.valueOf(application.getId().toString()));
        applicationRoleService.setRolesForApplication(Long.valueOf(application.getId().toString()), roles);
        notifyUsers(roles);

        ApprovalHistory history = new ApprovalHistory();
        history.saveAssignment(Long.valueOf(histories.size()), Long.valueOf(application.getId().toString()), LocalDateTime.now() );
        histories.add(history);
        
        //Debug code
        System.out.println("*** Showing history on resubmit():");
        showHistory();
    }

    @Override
    public void sendBack(String comment, boolean toApplicant, List<Map<String, String>> correctableFields) {
        List<Role> roles = new ArrayList<>();
        if(toApplicant) {
            roles = Arrays.asList(Role.APPLICANT);
            application.setApprovalStatus(ApprovalStatus.RETURNED);

        }
        else {
            List<ApprovalStep> prevSteps = approvalStepService.getPreviousSteps(ApplicationType.LEAVE_APPLICATION, new ArrayList<>(), application.getPathNo(), application.getCurrentStepNo());
            roles = getRoles(prevSteps);
            applicationRoleService.removeRolesForApplication(Long.valueOf(application.getId().toString()));
            applicationRoleService.setRolesForApplication(Long.valueOf(application.getId().toString()), roles);

            application.setApprovalStatus(ApprovalStatus.SENT_BACK);
            application.setPathNo(prevSteps.get(0).getPathNo());
            application.setCurrentStepNo(prevSteps.get(0).getStepNo());
        }

        notifyUsers(roles);

        ApprovalHistory sendBackHistory = histories.get(histories.size() - 1);
        sendBackHistory.saveAction(1L, 1L, 1L, LocalDateTime.now(), ApprovalActionType.SENT_BACK, comment, correctableFields, Arrays.asList(1L, 4L));

        ApprovalHistory history = new ApprovalHistory();
        history.saveAssignment(Long.valueOf(histories.size()), Long.valueOf(application.getId().toString()), LocalDateTime.now() );
        histories.add(history);

        //Debug code
        System.out.println("*** Showing history on sendBack():");
        showHistory();
    }

    @Override
    public void forward(String comment) {
        // TODO: add validation

        List<ApprovalStep> nextSteps = approvalStepService.getNextStep(ApplicationType.LEAVE_APPLICATION, application.getPathNo(), application.getCurrentStepNo());
        // TODO: handle exception

        application.setApprovalStatus(ApprovalStatus.PENDING);      // TODO: determine if this is required
        application.setPathNo(nextSteps.get(0).getPathNo());        // TODO: determine if this is required
        application.setCurrentStepNo(nextSteps.get(0).getStepNo());

        // TODO: use stream/map
        List<Role> roles = getRoles(nextSteps);
        // TODO: single method for the next two process
        applicationRoleService.removeRolesForApplication(Long.valueOf(application.getId().toString()));
        applicationRoleService.setRolesForApplication(Long.valueOf(application.getId().toString()), roles);

        // TODO: single method to handle below two action
        // Update existing history for action
        ApprovalHistory currentHistory = histories.get(histories.size() - 1);
        currentHistory.saveAction(1L, 1L, 1L, LocalDateTime.now(), ApprovalActionType.FORWARD, comment, null, Arrays.asList(1L, 4L));

        // Add new history for next reviewer
        ApprovalHistory history = new ApprovalHistory();
        history.saveAssignment(Long.valueOf(histories.size()), Long.valueOf(application.getId().toString()), LocalDateTime.now() );
        histories.add(history);

        notifyUsers(roles);

        //Debug code
        System.out.println("*** Showing history on forward():");
        showHistory();
    }

    @Override
    public void approve(String comment) {
        validateState(ApprovalStatus.APPROVED);
        application.setApprovalStatus(ApprovalStatus.APPROVED);
        applicationRoleService.removeRolesForApplication(Long.valueOf(application.getId().toString()));
        ApprovalHistory approvalHistory = histories.get(histories.size() - 1);
        approvalHistory.saveAction(1L, 1L, 1L, LocalDateTime.now(), ApprovalActionType.APPROVE, comment, null, Arrays.asList(5L, 8L));
        //Debug code
        System.out.println("*** Showing history on approve():");
        showHistory();
    }

    @Override
    public void reject(String comment) {
        validateState(ApprovalStatus.REJECTED);
        application.setApprovalStatus(ApprovalStatus.REJECTED);
        ApprovalHistory approvalHistory = histories.get(histories.size() - 1);
        applicationRoleService.removeRolesForApplication(Long.valueOf(application.getId().toString()));
        approvalHistory.saveAction(1L, 1L, 1L, LocalDateTime.now(), ApprovalActionType.REJECT, comment, null, Arrays.asList(5L, 8L));
        //Debug code
        System.out.println("*** Showing history on reject():");
        showHistory();
    }

    private void validateState(ApprovalStatus newStatus) {
        switch (application.getApprovalStatus()) {
            case RETURNED -> {
                if (!newStatus.equals(ApprovalStatus.PENDING)) {
                    throw new RuntimeException("Can't move to this state");
                }
            }
        }
    }

    private List<Role> getRoles(List<ApprovalStep> steps) {
        List<Role> roles = new ArrayList<>();
        for(ApprovalStep step : steps) {
            roles.add(step.getReviewerRole());
        }
        return roles;
    }

    private void notifyUsers(List<Role> roles) {
        //Notify the assignees of these roles
        System.out.println("Notifying users");
        for(Role role : roles) {
            System.out.println(role + " ");
        }
    }

    private void showHistory() {
        System.out.println(">>> >>> >>> History start");
        for(ApprovalHistory history : histories) {
            System.out.println(
                    "ID: " + history.getId() +
                    ", Application ID: " + history.getApplicationId() +
                    ",  User ID: " + history.getUserId() +
                    ", Employee ID: " + history.getEmployeeId() +
                    ", Office Post History ID: " + history.getOfficePostHistoryId() +
                    ", Action Type: " + history.getActionType() +
                    ", Assigned at: " + history.getAssignedAt() +
                    ", Reviewed at: " + history.getReviewedAt() +
                    ", Comment: " + history.getComment() +
                    ", Correctable Fields: " + history.getCorrectableFields() +
                    ", Users:" + history.getAssignedUsers()

            );
        }
        System.out.println("<<< <<< <<< History end");
    }
}