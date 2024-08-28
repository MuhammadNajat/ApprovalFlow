package com.dsi.approvalflow.service;

import com.dsi.approvalflow.mockentity.ApplicationType;
import com.dsi.approvalflow.mockentity.AllowedApprovalActions;
import com.dsi.approvalflow.mockentity.ApprovalStep;
import com.dsi.approvalflow.mockentity.Role;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApprovalStepService {

    public List<ApprovalStep> getApprovalSteps(ApplicationType applicationType, Role applicantRole) {
        List<ApprovalStep> steps = new ArrayList<>();

        ApprovalStep step1 = new ApprovalStep();
        step1.setId(1L);
        step1.setApplicationType(ApplicationType.LEAVE_APPLICATION);
        step1.setApplicantRole(Role.APPLICANT);
        step1.setPath(0);
        step1.setLevel(0);
        step1.setReviewerRole(Role.ASSISTANT_CONTROLLER);
        step1.setStartOverOnResubmit(false);
        step1.setAllowedApprovalActions(Arrays.asList(
                AllowedApprovalActions.SEND_BACK,
                AllowedApprovalActions.REQUEST_CHANGES,
                AllowedApprovalActions.FORWARD
        ));

        ApprovalStep step2 = new ApprovalStep();
        step2.setId(2L);
        step2.setApplicationType(ApplicationType.LEAVE_APPLICATION);
        step2.setApplicantRole(Role.APPLICANT);
        step2.setPath(0);
        step2.setLevel(1);
        step2.setReviewerRole(Role.CONTROLLER);
        step2.setStartOverOnResubmit(false);
        step2.setAllowedApprovalActions(Arrays.asList(
                AllowedApprovalActions.SEND_BACK,
                AllowedApprovalActions.REQUEST_CHANGES,
                AllowedApprovalActions.FORWARD
        ));

        ApprovalStep step3 = new ApprovalStep();
        step3.setId(3L);
        step3.setApplicationType(ApplicationType.LEAVE_APPLICATION);
        step3.setApplicantRole(Role.APPLICANT);
        step3.setPath(0);
        step3.setLevel(2);
        step3.setReviewerRole(Role.SECRETARY);
        step3.setStartOverOnResubmit(false);
        step3.setAllowedApprovalActions(Arrays.asList(
                AllowedApprovalActions.APPROVE,
                AllowedApprovalActions.REJECT
        ));

        steps.add(step1);
        steps.add(step2);
        steps.add(step3);

        return steps;
    }

    public List<Role> getReviewerRoles(ApplicationType applicationType, String applicantRole, Integer path, Integer level) {
        List<Role> roles = new ArrayList<>();
        for(ApprovalStep approvalStep : getApprovalSteps(applicationType, Role.APPLICANT)) {
            if(approvalStep.getPath() == path && approvalStep.getLevel() == level) {
                roles.add(approvalStep.getReviewerRole());
            }
        }
        return roles;
    }

    public List<ApprovalStep> getNextSteps(ApplicationType applicationType, List<Role> roles, Integer path, Integer level) {
        List<ApprovalStep> steps = getApprovalSteps(applicationType, Role.APPLICANT);
        List<ApprovalStep> nextSteps = new ArrayList<>();
        if(path == null) {
            nextSteps.add(steps.get(0));
            return nextSteps;
        }
        for (ApprovalStep step : steps) {
            if (applicationType == step.getApplicationType() &&
                    step.getPath() == path && step.getLevel() == (level + 1)) {
                nextSteps.add(step);
            }
        }
        return nextSteps;
    }

    public List<ApprovalStep> getPreviousSteps(ApplicationType applicationType, List<Role> roles, Integer path, Integer level) {
        if(path == null || level == null) {
            return null;
        }
        List<ApprovalStep> steps = getApprovalSteps(applicationType, Role.APPLICANT);
        List<ApprovalStep> prevSteps = new ArrayList<>();
        for (ApprovalStep step : steps) {
            if (applicationType == step.getApplicationType() &&
                    step.getPath() == path && step.getLevel() == (level - 1)) {
                prevSteps.add(step);
            }
        }
        return prevSteps;
    }

    public List<ApprovalStep> getCurrentSteps(ApplicationType applicationType, List<Role> roles, Integer path, Integer level) {
        if(path == null || level == null) {
            return null;
        }
        List<ApprovalStep> steps = getApprovalSteps(applicationType, Role.APPLICANT);
        List<ApprovalStep> currentSteps = new ArrayList<>();
        for (ApprovalStep step : steps) {
            if (applicationType == step.getApplicationType() &&
                    step.getPath() == path && step.getLevel() == level) {
                currentSteps.add(step);
            }
        }
        return currentSteps;
    }
}
