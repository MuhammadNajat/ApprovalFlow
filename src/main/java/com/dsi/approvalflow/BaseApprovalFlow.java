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

public class BaseApprovalFlow implements ApprovalFlow {
    private Application application;
    private List<ApprovalHistory> histories = new ArrayList<>();
    private User currentUser;

    //Dummy services
    private ApprovalStepService approvalStepService = new ApprovalStepService();
    private ApplicationRoleService applicationRoleService = new ApplicationRoleService();
    private UserService userService = new UserService();

    public BaseApprovalFlow(Application application) {
        this.application = application;
    }

    @Override
    public void submit() {
        System.out.println(">>> >>> >>> Entered Submit");
        try {
            validateState(ApprovalStatus.PENDING);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return;
        }

        List<ApprovalStep> initialSteps = new ArrayList<>();
        try {
            initialSteps = approvalStepService.getInitialStep(ApplicationType.LEAVE_APPLICATION, currentUser.getRoles());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        application.setApprovalStatus(ApprovalStatus.PENDING);
        application.setInternalStatus(InternalStatus.SUBMITTED);

        application.setPathNo(initialSteps.get(0).getPathNo());
        application.setCurrentStepNo(initialSteps.get(0).getStepNo());

        List<Role> roles = initialSteps.stream()
                .map(ApprovalStep::getReviewerRole)
                .distinct()
                .collect(Collectors.toList());

        applicationRoleService.setRolesForApplication(application.getId(), roles);


        ApprovalHistory history = new ApprovalHistory();
        history.saveAssignment(Long.valueOf(histories.size()), application.getId(), LocalDateTime.now());

        histories.add(history);

        // TODO: async / aspect & integrate with notification service
        notifyUsers(roles);

        //Debug code
        System.out.println("*** Showing history on submit():");
        showHistory();
        System.out.println("<<< <<< <<< Exiting Submit");
    }

    @Override
    public void resubmit() {
        System.out.println(">>> >>> >>> Entered Resubmit");
        List<ApprovalStep> currentStep = new ArrayList<>();

        try {
            validateState(ApprovalStatus.PENDING);
            currentStep = approvalStepService.getStepWithPathAndStepNo(ApplicationType.LEAVE_APPLICATION,
                    currentUser.getRoles(), application.getPathNo(), application.getCurrentStepNo());
        } catch (RuntimeException e) {
            e.printStackTrace();
            return;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        List<ApprovalStep> nextStep = new ArrayList<>();

        nextStep = approvalStepService.getInitialStep(ApplicationType.LEAVE_APPLICATION, currentUser.getRoles());
        if (!currentStep.get(0).getStartOverOnResubmit()) {
            try {
                nextStep = approvalStepService.getStepWithPathAndStepNo(ApplicationType.LEAVE_APPLICATION,
                        currentUser.getRoles(), application.getPathNo(), application.getCurrentStepNo());
            } catch (Exception e) {
                e.printStackTrace();
                //Empty step list returned
                return;
            }
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
        String assigneesCommaSeparated = getCommaSeparatedEmployeeIds(nextStepRoles);
        resubmitHistory.saveAction(currentUser.getId(), 1L, 1L, LocalDateTime.now(),
                ApprovalActionType.RESUBMIT, null, null, assigneesCommaSeparated);

        ApprovalHistory history = new ApprovalHistory();
        history.saveAssignment(Long.valueOf(histories.size()), application.getId(), LocalDateTime.now());
        histories.add(history);

        notifyUsers(nextStepRoles);

        //Debug code
        System.out.println("*** Showing history on resubmit():");
        showHistory();

        System.out.println(">>> >>> >>> Exiting Resubmit");
    }

    @Override
    public void sendBack(String comment, boolean toApplicant, List<Map<String, String>> correctableFields) {
        System.out.println(">>> >>> >>> Entered sendBack");

        List<ApprovalStep> currentStep = new ArrayList<>();
        try {
            currentStep = approvalStepService.getStepWithPathAndStepNo(ApplicationType.LEAVE_APPLICATION,
                    currentUser.getRoles(), application.getPathNo(), application.getCurrentStepNo());
        } catch (Exception e) {
            //If no step found
            e.printStackTrace();
            return;
        }
        boolean returnsToApplicant = toApplicant || currentStep.get(0).getStepNo() == 0;
        ApprovalStatus nextStatus = returnsToApplicant ? ApprovalStatus.RETURNED : ApprovalStatus.SENT_BACK;

        try {
            validateState(nextStatus);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return;
        }

        application.setApprovalStatus(nextStatus);

        List<Role> prevStepRoles = new ArrayList<>();

        if (!returnsToApplicant) {
            List<ApprovalStep> prevStep = approvalStepService.getPreviousStep(ApplicationType.LEAVE_APPLICATION,
                    currentUser.getRoles(), application.getPathNo(), application.getCurrentStepNo());
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
        String assigneesCommaSeparated = getCommaSeparatedEmployeeIds(prevStepRoles);
        sendBackHistory.saveAction(currentUser.getId(), 1L, 1L, LocalDateTime.now(),
                ApprovalActionType.SENT_BACK, comment, correctableFields, assigneesCommaSeparated);

        // Add new history for next reviewer
        ApprovalHistory history = new ApprovalHistory();
        history.saveAssignment(Long.valueOf(histories.size()), application.getId(), LocalDateTime.now());
        histories.add(history);

        notifyUsers(prevStepRoles);

        //Debug code
        System.out.println("*** Showing history on sendBack():");
        showHistory();

        System.out.println(">>> >>> >>> Exiting sendBack");
    }

    @Override
    public void forward(String comment) {
        System.out.println(">>> >>> >>> Entered forward");
        List<ApprovalStep> nextStep = new ArrayList<>();

        try {
            validateState(ApprovalStatus.PENDING);
            nextStep = approvalStepService.getNextStep(ApplicationType.LEAVE_APPLICATION, application.getPathNo(),
                    application.getCurrentStepNo());
        } catch (RuntimeException e) {
            e.printStackTrace();
            return;
        } catch (Exception e) {
            //If no step found
            e.printStackTrace();
            return;
        }

        application.setPathNo(nextStep.get(0).getPathNo());
        application.setCurrentStepNo(nextStep.get(0).getStepNo());

        List<Role> roles = nextStep.stream()
                .map(ApprovalStep::getReviewerRole)
                .distinct()
                .collect(Collectors.toList());

        applicationRoleService.setRolesForApplication(application.getId(), roles);

        // Update existing history for action
        ApprovalHistory currentHistory = histories.get(histories.size() - 1);
        String commaSeparatedAssignees = getCommaSeparatedEmployeeIds(roles);
        currentHistory.saveAction(currentUser.getId(), 1L, 1L, LocalDateTime.now(),
                ApprovalActionType.FORWARD, comment, null, commaSeparatedAssignees);

        // Add new history for next reviewer
        ApprovalHistory history = new ApprovalHistory();
        history.saveAssignment(Long.valueOf(histories.size()), application.getId(), LocalDateTime.now());
        histories.add(history);

        notifyUsers(roles);

        //Debug code
        System.out.println("*** Showing history on forward():");
        showHistory();

        System.out.println(">>> >>> >>> Exiting forward");
    }

    @Override
    public void approve(String comment) {
        System.out.println(">>> >>> >>> Entered approve");
        List<ApprovalStep> currentStep = new ArrayList<>();
        try {
            validateState(ApprovalStatus.APPROVED);
            currentStep = approvalStepService.getStepWithPathAndStepNo(application.getType(), currentUser.getRoles(),
                    application.getPathNo(), application.getCurrentStepNo());
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            //If no step found
            e.printStackTrace();
            return;
        }

        application.setApprovalStatus(ApprovalStatus.APPROVED);

        List<Role> currentStepRoles = currentStep.stream()
                .map(ApprovalStep::getReviewerRole)
                .distinct()
                .collect(Collectors.toList());

        String assigneesCommaSeparated = getCommaSeparatedEmployeeIds(currentStepRoles);
        ApprovalHistory approvalHistory = histories.get(histories.size() - 1);
        approvalHistory.saveAction(currentUser.getId(), 1L, 1L, LocalDateTime.now(),
                ApprovalActionType.APPROVE, comment, null, assigneesCommaSeparated);

        //Debug code
        System.out.println("*** Showing history on approve():");
        showHistory();
        System.out.println(">>> >>> >>> Exiting approve");

    }

    @Override
    public void reject(String comment) {
        System.out.println(">>> >>> >>> Entered Reject");

        List<ApprovalStep> currentStep = new ArrayList<>();
        try {
            validateState(ApprovalStatus.REJECTED);
            currentStep = approvalStepService.getStepWithPathAndStepNo(application.getType(), currentUser.getRoles(),
                    application.getPathNo(), application.getCurrentStepNo());
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            //If no step found
            e.printStackTrace();
            return;
        }

        application.setApprovalStatus(ApprovalStatus.REJECTED);
        applicationRoleService.removeRolesForApplication(application.getId());

        List<Role> roles = currentStep.stream()
                .map(ApprovalStep::getReviewerRole)
                .distinct()
                .collect(Collectors.toList());
        String assigneesCommaSeparated = getCommaSeparatedEmployeeIds(roles);

        ApprovalHistory approvalHistory = histories.get(histories.size() - 1);
        approvalHistory.saveAction(currentUser.getId(), 1L, 1L, LocalDateTime.now(),
                ApprovalActionType.REJECT, comment, null, assigneesCommaSeparated);

        //Debug code
        System.out.println("*** Showing history on reject():");
        showHistory();
        System.out.println(">>> >>> >>> Exiting Reject");
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
            List<User> fetchedUsers = userService.getUsersByRole(role);
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
        String response = "";
        for (Role role : roles) {
            List<User> usersWithRole = userService.getUsersByRole(role);
            for (User userWithRole : usersWithRole) {
                response += userWithRole.getId().toString();
                response += ",";
            }
        }
        response = response.length() > 0? response.substring(0, response.length() - 1) : "";
        return response;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
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
                            ", Users:" + history.getAssignedUsers()

            );
        }
        System.out.println("<<< <<< <<< History end");
    }
}