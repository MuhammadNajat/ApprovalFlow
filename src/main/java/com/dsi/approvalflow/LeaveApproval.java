package com.dsi.approvalflow;

import com.dsi.approvalflow.dto.ApprovalFlowStep;;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class LeaveApproval implements Approval {
    private List<ApprovalFlowStep> steps;
    private Integer currentApprovalLevel;

    public LeaveApproval(String configPath, String applicationType) {
        steps = ApprovalDataManager.getApprovalFlowSteps(configPath, applicationType);
        currentApprovalLevel = -1;
    }

    @Override
    public void submit() {
        System.out.println(">>> >>> >>> Entered submit:");
        ApprovalFlowStep nextStep = steps.get(currentApprovalLevel + 1);
        System.out.println("Current level: " + currentApprovalLevel);
        currentApprovalLevel++;
        System.out.println("Next level: " + currentApprovalLevel);
        System.out.println("Next step info:");
        printStepData(nextStep);
        System.out.println("<<< <<< <<< Exiting submit");
    }

    @Override
    public void sendBack(String comment, boolean toApplicant) {
        System.out.println(">>> >>> >>> Entered sendBack:");
        if(currentApprovalLevel == -1) {
            System.out.println("Application is with the applicant. Can't send it back further");
            System.out.println("<<< <<< <<< Exiting sendBack");
            return;
        }

        if(currentApprovalLevel == 0) {
            System.out.println("Current level: 0");
            System.out.println("Next level: -1");
            System.out.println("<<< <<< <<< Exiting sendBack");
            return;
        }

        ApprovalFlowStep nextStep = steps.get(currentApprovalLevel - 1);
        System.out.println("Send back to applicant: " + toApplicant);
        System.out.println("Current level: " + currentApprovalLevel);
        currentApprovalLevel = toApplicant? -1 : currentApprovalLevel - 1;
        System.out.println("Next level: " + currentApprovalLevel);
        System.out.println("Next step info:");
        printStepData(nextStep);
        System.out.println("<<< <<< <<< Exiting sendBack");
    }

    @Override
    public void forward(String comment) {
        System.out.println(">>> >>> >>> Entered forward:");
        if(currentApprovalLevel == steps.size() - 1) {
            System.out.println("No next step exists!");
            System.out.println("<<< <<< <<< Exiting forward");
            return;
        }
        ApprovalFlowStep nextStep = steps.get(currentApprovalLevel + 1);
        System.out.println("Current level: " + currentApprovalLevel);
        currentApprovalLevel++;
        System.out.println("Next level: " + currentApprovalLevel);
        System.out.println("Next step info:");
        printStepData(nextStep);
        System.out.println("<<< <<< <<< Exiting forward");
    }

    @Override
    public void approve(String comment) {
        System.out.println(">>> >>> >>> Entered approve:");
        System.out.println("Current level: " + currentApprovalLevel);
        System.out.println("<<< <<< <<< Exiting approve");
    }

    @Override
    public void reject(String comment) {
        System.out.println(">>> >>> >>> Entered reject:");
        System.out.println("Current level: " + currentApprovalLevel);
        System.out.println("<<< <<< <<< Exiting reject");
    }

    @Override
    public void sendBackForCorrection(String comment, boolean toApplicant, List<Map<String, String>> correctableFields) {
        System.out.println(">>> >>> >>> Entered sendBackForCorrection:");
        System.out.println("Send back to applicant: " + toApplicant);
        ApprovalFlowStep nextStep = steps.get(currentApprovalLevel - 1);
        System.out.println("Current level: " + currentApprovalLevel);
        currentApprovalLevel = toApplicant? -1 : currentApprovalLevel - 1;
        System.out.println("Next level: " + currentApprovalLevel);
        System.out.println("Next step info:");
        printStepData(nextStep);
        System.out.println("<<< <<< <<< Exiting sendForCorrection:");
    }

    private void printStepData(ApprovalFlowStep step) {
        System.out.println("Approver:");
        System.out.println("Type: " + step.getApprover().getType() +
                ", Post Id: " + step.getApprover().getPostId() +
                ", Role: " + step.getApprover().getRole() +
                ", Office Layer Id: " + step.getApprover().getOfficeLayerId()
        );
        System.out.println("Allowed actions: ");
        for(String access : step.getAllowedActions()) {
            System.out.println(access + " ");
        }
    }
}