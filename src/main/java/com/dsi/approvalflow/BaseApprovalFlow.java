package com.dsi.approvalflow;

import com.dsi.approvalflow.mockentity.User;
import com.dsi.approvalflow.mockentity.ApplicationType;
import com.dsi.approvalflow.mockentity.*;
import com.dsi.approvalflow.service.ApplicationRoleService;
import com.dsi.approvalflow.service.ApprovalStepService;
import com.dsi.approvalflow.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public abstract class BaseApprovalFlow implements ApprovalFlow {
    private Application application;
    private List<ApprovalHistory> histories = new ArrayList<>();

    //Dummy services
    private ApprovalStepService approvalStepService = new ApprovalStepService();
    private ApplicationRoleService applicationRoleService = new ApplicationRoleService();
    private UserService UserService = new UserService();

    public BaseApprovalFlow(Application application) {
        this.application = application;
    }

    @Override
    public void submit() {
        try {
            System.out.println(">>> >>> >>> Entered Submit");
            validateState(ApprovalStatus.PENDING);

            List<ApprovalStep> initialSteps = approvalStepService
                    .getInitialStep(application.getType(), UserService.getCurrentUser().getRoles());

            application.setPathNo(initialSteps.get(0).getPathNo());
            application.setCurrentStepNo(initialSteps.get(0).getStepNo());
            application.setApprovalStatus(ApprovalStatus.PENDING);
            application.setInternalStatus(InternalStatus.SUBMITTED);

            List<Role> reviewerRoles = initialSteps.stream()
                    .map(ApprovalStep::getReviewerRole)
                    .distinct()
                    .collect(Collectors.toList());

            applicationRoleService.setRolesForApplication(application.getId(), reviewerRoles);

            ApprovalHistory history = new ApprovalHistory(Long.valueOf(histories.size()), application.getId(), LocalDateTime.now());

            // TODO: persist in db
            histories.add(history);

            // TODO: async / aspect & integrate with notification service
            notifyUsers(reviewerRoles);

            //Debug code
            System.out.println("*** Showing history on submit():");
            showHistory();
            System.out.println("<<< <<< <<< Exiting Submit");
        } catch (Exception e) {
            //TODO: Catch different exceptions
            //TODO: Write custom exceptions for validation, etc.
            e.printStackTrace();
        }
    }

    @Override
    public void resubmit() {
        try {
            System.out.println(">>> >>> >>> Entered Resubmit");
            List<ApprovalStep> currentStep = new ArrayList<>();

            validateState(ApprovalStatus.PENDING);
            currentStep = approvalStepService.getStepWithPathAndStepNo(ApplicationType.LEAVE_APPLICATION,
                    UserService.getCurrentUser().getRoles(), application.getPathNo(), application.getCurrentStepNo());


            List<ApprovalStep> nextStep = new ArrayList<>();

            nextStep = approvalStepService.getInitialStep(ApplicationType.LEAVE_APPLICATION, UserService.getCurrentUser().getRoles());
            if (!currentStep.get(0).getStartOverOnResubmit()) {
                nextStep = approvalStepService.getStepWithPathAndStepNo(ApplicationType.LEAVE_APPLICATION,
                        UserService.getCurrentUser().getRoles(), application.getPathNo(), application.getCurrentStepNo());
            }

            application.setPathNo(nextStep.get(0).getPathNo());
            application.setCurrentStepNo(nextStep.get(0).getStepNo());
            application.setApprovalStatus(ApprovalStatus.PENDING);


            List<Role> nextStepRoles = nextStep.stream()
                    .map(ApprovalStep::getReviewerRole)
                    .distinct()
                    .collect(Collectors.toList());

            applicationRoleService.setRolesForApplication(application.getId(), nextStepRoles);

            ApprovalHistory resubmitHistory = histories.get(histories.size() - 1);
            resubmitHistory.saveAction(UserService.getCurrentUser().getId(), 1L, 1L, LocalDateTime.now(),
                    ApprovalActionType.RESUBMIT, null, null, null);

            ApprovalHistory history = new ApprovalHistory(Long.valueOf(histories.size()), application.getId(), LocalDateTime.now());
            histories.add(history);

            notifyUsers(nextStepRoles);

            //Debug code
            System.out.println("*** Showing history on resubmit():");
            showHistory();

            System.out.println(">>> >>> >>> Exiting Resubmit");
        } catch (Exception e) {
            //TODO: Catch different exceptions
            //TODO: Write custom exceptions for validation, etc.
            e.printStackTrace();
        }
    }

    @Override
    public void sendBack(String comment, boolean toApplicant, List<Map<String, String>> correctableFields) {
        try {
            System.out.println(">>> >>> >>> Entered sendBack");
            List<ApprovalStep> currentStep = new ArrayList<>();
            currentStep = approvalStepService.getStepWithPathAndStepNo(ApplicationType.LEAVE_APPLICATION,
                    UserService.getCurrentUser().getRoles(), application.getPathNo(), application.getCurrentStepNo());

            //If reviewer is at step no 0, sending back will enable return to applicant
            boolean returnsToApplicant = toApplicant || currentStep.get(0).getStepNo() == 0;
            ApprovalStatus nextStatus = returnsToApplicant ? ApprovalStatus.RETURNED : ApprovalStatus.SENT_BACK;

            validateState(nextStatus);

            application.setApprovalStatus(nextStatus);

            List<Role> prevStepRoles = new ArrayList<>();

            if (!returnsToApplicant) {
                List<ApprovalStep> prevStep = approvalStepService.getPreviousStep(ApplicationType.LEAVE_APPLICATION,
                        UserService.getCurrentUser().getRoles(), application.getPathNo(), application.getCurrentStepNo());
                prevStepRoles = prevStep.stream()
                        .map(ApprovalStep::getReviewerRole)
                        .distinct()
                        .collect(Collectors.toList());

                application.setPathNo(prevStep.get(0).getPathNo());
                application.setCurrentStepNo(prevStep.get(0).getStepNo());

                applicationRoleService.setRolesForApplication(application.getId(), prevStepRoles);
            }

            // Update existing history for action
            ApprovalHistory sendBackHistory = histories.get(histories.size() - 1);
            List<Role> currentStepRoles = currentStep.stream()
                    .map(ApprovalStep::getReviewerRole)
                    .distinct()
                    .collect(Collectors.toList());
            String assigneesCommaSeparated = getCommaSeparatedEmployeeIds(currentStepRoles);
            sendBackHistory.saveAction(UserService.getCurrentUser().getId(), 1L, 1L, LocalDateTime.now(),
                    ApprovalActionType.SENT_BACK, comment, correctableFields, assigneesCommaSeparated);

            // Add new history for next reviewer
            ApprovalHistory history = new ApprovalHistory(Long.valueOf(histories.size()), application.getId(), LocalDateTime.now());
            histories.add(history);

            notifyUsers(prevStepRoles);

            //Debug code
            System.out.println("*** Showing history on sendBack():");
            showHistory();

            System.out.println(">>> >>> >>> Exiting sendBack");
        } catch (Exception e) {
            //TODO: Catch different exceptions
            //TODO: Write custom exceptions for validation, etc.
            e.printStackTrace();
        }
    }

    @Override
    public void forward(String comment) {
        try {
            System.out.println(">>> >>> >>> Entered forward");
            validateState(ApprovalStatus.PENDING);
            List<ApprovalStep> currentStep = new ArrayList<>();
            currentStep = approvalStepService.getStepWithPathAndStepNo(ApplicationType.LEAVE_APPLICATION,
                    UserService.getCurrentUser().getRoles(), application.getPathNo(), application.getCurrentStepNo());

            List<ApprovalStep> nextStep = new ArrayList<>();
            nextStep = approvalStepService.getNextStep(ApplicationType.LEAVE_APPLICATION, application.getPathNo(),
                    application.getCurrentStepNo());

            application.setPathNo(nextStep.get(0).getPathNo());
            application.setCurrentStepNo(nextStep.get(0).getStepNo());

            List<Role> roles = nextStep.stream()
                    .map(ApprovalStep::getReviewerRole)
                    .distinct()
                    .collect(Collectors.toList());

            applicationRoleService.setRolesForApplication(application.getId(), roles);

            // Update existing history for action
            ApprovalHistory currentHistory = histories.get(histories.size() - 1);
            List<Role> currentStepRoles = currentStep.stream()
                    .map(ApprovalStep::getReviewerRole)
                    .distinct()
                    .collect(Collectors.toList());
            String commaSeparatedAssignees = getCommaSeparatedEmployeeIds(currentStepRoles);
            currentHistory.saveAction(UserService.getCurrentUser().getId(), 1L, 1L, LocalDateTime.now(),
                    ApprovalActionType.FORWARD, comment, null, commaSeparatedAssignees);

            // Add new history for next reviewer
            ApprovalHistory history = new ApprovalHistory(Long.valueOf(histories.size()), application.getId(), LocalDateTime.now());
            histories.add(history);

            notifyUsers(roles);

            //Debug code
            System.out.println("*** Showing history on forward():");
            showHistory();

            System.out.println(">>> >>> >>> Exiting forward");
        } catch (Exception e) {
            //TODO: Catch different exceptions
            //TODO: Write custom exceptions for validation, etc.
            e.printStackTrace();
        }
    }

    @Override
    public void approve(String comment) {
        try {
            System.out.println(">>> >>> >>> Entered approve");
            validateState(ApprovalStatus.APPROVED);
            List<ApprovalStep> currentStep = new ArrayList<>();
            currentStep = approvalStepService.getStepWithPathAndStepNo(application.getType(), UserService.getCurrentUser().getRoles(),
                    application.getPathNo(), application.getCurrentStepNo());

            application.setApprovalStatus(ApprovalStatus.APPROVED);

            List<Role> currentStepRoles = currentStep.stream()
                    .map(ApprovalStep::getReviewerRole)
                    .distinct()
                    .collect(Collectors.toList());

            String assigneesCommaSeparated = getCommaSeparatedEmployeeIds(currentStepRoles);
            ApprovalHistory approvalHistory = histories.get(histories.size() - 1);
            approvalHistory.saveAction(UserService.getCurrentUser().getId(), 1L, 1L, LocalDateTime.now(),
                    ApprovalActionType.APPROVE, comment, null, assigneesCommaSeparated);

            //Debug code
            System.out.println("*** Showing history on approve():");
            showHistory();
            System.out.println(">>> >>> >>> Exiting approve");
        } catch (Exception e) {
            //TODO: Catch different exceptions
            //TODO: Write custom exceptions for validation, etc.
            e.printStackTrace();
        }
    }

    @Override
    public void reject(String comment) {
        try {
            System.out.println(">>> >>> >>> Entered Reject");
            validateState(ApprovalStatus.REJECTED);
            List<ApprovalStep> currentStep = new ArrayList<>();
            currentStep = approvalStepService.getStepWithPathAndStepNo(application.getType(), UserService.getCurrentUser().getRoles(),
                    application.getPathNo(), application.getCurrentStepNo());

            application.setApprovalStatus(ApprovalStatus.REJECTED);
            applicationRoleService.removeRolesForApplication(application.getId());

            List<Role> currentStepRoles = currentStep.stream()
                    .map(ApprovalStep::getReviewerRole)
                    .distinct()
                    .collect(Collectors.toList());
            String assigneesCommaSeparated = getCommaSeparatedEmployeeIds(currentStepRoles);

            ApprovalHistory approvalHistory = histories.get(histories.size() - 1);
            approvalHistory.saveAction(UserService.getCurrentUser().getId(), 1L, 1L, LocalDateTime.now(),
                    ApprovalActionType.REJECT, comment, null, assigneesCommaSeparated);

            //Debug code
            System.out.println("*** Showing history on reject():");
            showHistory();
            System.out.println(">>> >>> >>> Exiting Reject");
        } catch (Exception e) {
            //TODO: Catch different exceptions
            //TODO: Write custom exceptions for validation, etc.
            e.printStackTrace();
        }
    }

    private void validateState(ApprovalStatus newStatus) {
        ApprovalStatus status = application.getApprovalStatus();
        if (status == null && newStatus == ApprovalStatus.PENDING) {
            return;
        } else if (status == null) {
            throw new RuntimeException("Can't move to this state");
        }

        switch (status) {
            case RETURNED -> {
                if (!newStatus.equals(ApprovalStatus.PENDING)) {
                    throw new RuntimeException("Can't move to this state");
                }
            }
        }
    }

    private void notifyUsers(List<Role> roles) {
        //Notify the assignees of these roles
        List<User> assignedUsers = new ArrayList<>();
        for (Role role : roles) {
            List<User> fetchedUsers = UserService.getUsersByRole(role);
            for (User fetchedUser : fetchedUsers) {
                if (!assignedUsers.contains(fetchedUser)) {
                    assignedUsers.add(fetchedUser);
                }
            }
            System.out.println("Notifying: ");
            for (User assignedUser : assignedUsers) {
                System.out.print(assignedUser.getId() + " ");
            }
            System.out.println("");
        }
        System.out.println("Notifying users");
        for (Role role : roles) {
            System.out.println(role + " ");
        }
    }

    //TODO - Return comma separated Assignee Employee IDs
    //Returned comma separated user ids. However, it should return comma separated employee ids.
    private String getCommaSeparatedEmployeeIds(List<Role> roles) {
        List<User> users = new ArrayList<>();
        List<String> idTexts = new ArrayList<>();
        for (Role role : roles) {
            List<User> usersWithRole = UserService.getUsersByRole(role);
            for (User userWithRole : usersWithRole) {
                if (!users.contains(userWithRole)) {
                    users.add(userWithRole);
                    idTexts.add(userWithRole.getId().toString());
                }
            }
        }

        return String.join(",", idTexts);
    }

    private void showHistory() {
        System.out.println(">>> >>> >>> History start");
        for (ApprovalHistory history : histories) {
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
                            ", Users:" + history.getAssignedEmployeeIds()

            );
        }
        System.out.println("<<< <<< <<< History end");
    }
}