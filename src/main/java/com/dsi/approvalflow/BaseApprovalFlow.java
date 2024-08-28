package com.dsi.approvalflow;

import com.dsi.approvalflow.mockentity.ApplicationType;
import com.dsi.approvalflow.mockentity.*;
import com.dsi.approvalflow.service.ApplicationRoleService;
import com.dsi.approvalflow.service.ApprovalStepService;

import java.time.LocalDateTime;
import java.util.*;

;

public class BaseApprovalFlow implements ApprovalFlow {
    private Application<?> application;
    private List<ApprovalStep> approvalSteps;
    private List<ApprovalHistory> histories = new ArrayList<>();
    private ApplicationType applicationType;


    private Map<Long, List<Role>> applicationToRole = new HashMap<>();

    //Dummy services
    private ApprovalStepService approvalStepService = new ApprovalStepService();
    private ApplicationRoleService applicationRoleService = new ApplicationRoleService();

    public BaseApprovalFlow(ApplicationType applicationType, Application<?> application) {
        this.applicationType = applicationType;
        //approvalSteps = approvalStepService.getApprovalSteps(ApplicationType.LEAVE_APPLICATION, "EMPLOYEE");
        this.application = application;
    }

    @Override
    public void submit() {
        List<ApprovalStep> nextSteps = approvalStepService.getNextSteps(ApplicationType.LEAVE_APPLICATION, new ArrayList<>(), null, null);

        application.setApprovalStatus(ApprovalStatus.PENDING);
        application.setInternalStatus(InternalStatus.SUBMITTED);
        application.setPath(nextSteps.get(0).getPath());
        application.setCurrentLevel(nextSteps.get(0).getLevel());


        List<Role> roles = getRoles(nextSteps);
        applicationRoleService.setRolesForApplication(Long.valueOf(application.getId().toString()), roles);

        notifyUsers(roles);

        ApprovalHistory history = new ApprovalHistory();
        history.saveAssignment(Long.valueOf(histories.size()), Long.valueOf(application.getId().toString()), LocalDateTime.now() );

        histories.add(history);

        //Debug code
        System.out.println("*** Showing history on submit():");
        showHistory();
    }

    @Override
    public void resubmit() {
        ApprovalHistory resubmitHistory = histories.get(histories.size() - 1);
        resubmitHistory.saveAction(1L, 1L, 1L, LocalDateTime.now(), ApprovalActionType.RESUBMIT, null, null, Arrays.asList(1L, 4L));

        application.setApprovalStatus(ApprovalStatus.PENDING);

        List<ApprovalStep> currentSteps = approvalStepService.getCurrentSteps(ApplicationType.LEAVE_APPLICATION, new ArrayList<>(), application.getPath(), application.getCurrentLevel());

        List<ApprovalStep> nextSteps = approvalStepService.getCurrentSteps(ApplicationType.LEAVE_APPLICATION, new ArrayList<>(), application.getPath(), application.getCurrentLevel());;
        if(currentSteps.get(0).getStartOverOnResubmit()) {
            nextSteps = approvalStepService.getNextSteps(ApplicationType.LEAVE_APPLICATION, new ArrayList<>(), null, null);
        }

        application.setPath(nextSteps.get(0).getPath());
        application.setCurrentLevel(nextSteps.get(0).getLevel());

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
            List<ApprovalStep> prevSteps = approvalStepService.getPreviousSteps(ApplicationType.LEAVE_APPLICATION, new ArrayList<>(), application.getPath(), application.getCurrentLevel());
            roles = getRoles(prevSteps);
            applicationRoleService.removeRolesForApplication(Long.valueOf(application.getId().toString()));
            applicationRoleService.setRolesForApplication(Long.valueOf(application.getId().toString()), roles);

            application.setApprovalStatus(ApprovalStatus.SENT_BACK);
            application.setPath(prevSteps.get(0).getPath());
            application.setCurrentLevel(prevSteps.get(0).getLevel());
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
        ApprovalHistory forwardHistory = histories.get(histories.size() - 1);
        forwardHistory.saveAction(1L, 1L, 1L, LocalDateTime.now(), ApprovalActionType.FORWARD, comment, null, Arrays.asList(1L, 4L));

        List<ApprovalStep> nextSteps = approvalStepService.getNextSteps(ApplicationType.LEAVE_APPLICATION, new ArrayList<>(), application.getPath(), application.getCurrentLevel());

        List<Role> roles = getRoles(nextSteps);
        applicationRoleService.removeRolesForApplication(Long.valueOf(application.getId().toString()));
        applicationRoleService.setRolesForApplication(Long.valueOf(application.getId().toString()), roles);

        notifyUsers(roles);

        application.setApprovalStatus(ApprovalStatus.PENDING);
        if(!nextSteps.isEmpty()) {
            application.setPath(nextSteps.get(0).getPath());
            application.setCurrentLevel(nextSteps.get(0).getLevel());
        }

        ApprovalHistory history = new ApprovalHistory();
        history.saveAssignment(Long.valueOf(histories.size()), Long.valueOf(application.getId().toString()), LocalDateTime.now() );
        histories.add(history);

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