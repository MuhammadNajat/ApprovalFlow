package com.dsi.approvalflow;

import com.dsi.approvalflow.dto.ApplicationType;
import com.dsi.approvalflow.dto.approval.ApprovalFlowStep;
import com.dsi.approvalflow.dto.approval.ApprovalActorType;
import com.dsi.approvalflow.dto.approval.ApprovalState;
import com.dsi.approvalflow.dto.approval.ReviewerType;
import com.dsi.approvalflow.mockentity.*;
import com.dsi.approvalflow.util.ApprovalDataManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

;

public class BaseApprovalFlow implements ApprovalFlow {
    private Application<?> application;
    private List<ApprovalFlowStep> steps;
    private String configPath = "approvalFlow.json";
    private List<ApprovalHistory> histories = new ArrayList<>();
    private ApplicationType applicationType;

    public BaseApprovalFlow(ApplicationType applicationType, Application<?> application) {
        this.applicationType = applicationType;
        steps = ApprovalDataManager.getApprovalFlowSteps(configPath, applicationType);
        this.application = application;
    }

    @Override
    public void submit() {
        validateState(ApprovalState.PENDING);
        ApprovalFlowStep step = steps.get(0);
        OfficePost officePost = getOfficePost(step);
        application.setStatus(ApprovalState.PENDING);
        application.setCurrentReviewerType(ApprovalActorType.POST);
        application.setCurrentReviewerId(officePost.getId());

        ApprovalHistory history = new ApprovalHistory();
        history.saveAssignment(LocalDateTime.now(), ApprovalActorType.POST, officePost.getId());
        histories.add(history);
    }

    private void validateState(ApprovalState newState) {
        switch (application.getStatus()){
            case DRAFT -> {
                if (!newState.equals(ApprovalState.PENDING)) {
                    throw new RuntimeException("Can't move to this state");
                }
            }
        }
    }

    @Override
    public void sendBack(String comment, boolean toApplicant) {
        validateState(ApprovalState.RETURNED);
        application.setStatus(ApprovalState.PENDING);
        returnApplication(comment, toApplicant, ApprovalActionType.RETURN, null);
    }

    @Override
    public void forward(String comment) {
        ApprovalHistory forwardHistory = new ApprovalHistory(ApprovalActionType.FORWARD, application.getCurrentReviewerType(),
                application.getCurrentReviewerId(), LocalDateTime.now(), null, comment);
        histories.add(forwardHistory);

        OfficePost currentApproverOfficePost = getPostFromActorTypeAndId(application.getCurrentReviewerType(),
                application.getCurrentReviewerId());
        ApprovalFlowStep nextStep = getNextStep(steps, currentApproverOfficePost);
        OfficePost officePost = getOfficePost(nextStep);

        application.setStatus(ApprovalState.PENDING);

        ApprovalActorType actorType = getReviewActorType(officePost);
        application.setCurrentReviewerType(actorType);

        Long reviewerId = getReviewUserId(officePost);
        application.setCurrentReviewerId(reviewerId);

        ApprovalHistory assignmentHistory = new ApprovalHistory(ApprovalActionType.ASSIGNED, actorType,
                reviewerId, LocalDateTime.now(), null, comment);
        histories.add(assignmentHistory);
    }

    @Override
    public void approve(String comment) {
        validateState(ApprovalState.APPROVED);
        application.setStatus(ApprovalState.APPROVED);
        ApprovalHistory approveHistory = new ApprovalHistory(ApprovalActionType.APPROVE, application.getCurrentReviewerType(),
                application.getCurrentReviewerId(), LocalDateTime.now(), null, comment);
    }

    @Override
    public void reject(String comment) {
        validateState(ApprovalState.REJECTED);
        application.setStatus(ApprovalState.REJECTED);
        ApprovalHistory approveHistory = new ApprovalHistory(ApprovalActionType.REJECT, application.getCurrentReviewerType(),
                application.getCurrentReviewerId(), LocalDateTime.now(), null, comment);
    }

    @Override
    public void sendBackForCorrection(String comment, boolean toApplicant, List<Map<String, String>> correctableFields) {
        validateState(ApprovalState.PENDING);
        application.setStatus(ApprovalState.PENDING);
        returnApplication(comment, toApplicant, ApprovalActionType.REQUEST_CORRECTIONS, correctableFields);
    }

    private void returnApplication(String comment, boolean toApplicant, ApprovalActionType actionType, List<Map<String, String>> correctableFields) {
        if(histories.size() == 0) {
            return;
        }

        ApprovalHistory sendBackHistory = new ApprovalHistory(actionType,
                application.getCurrentReviewerType(), application.getCurrentReviewerId(),
                LocalDateTime.now(), null, comment);
        histories.add(sendBackHistory);

        application.setStatus(ApprovalState.RETURNED);

        if(histories.size() == 1 || toApplicant) {
            application.setCurrentReviewerType(application.getApplicantType());
            application.setCurrentReviewerId(application.getApplicantId());

            ApprovalHistory assignmentHistory = new ApprovalHistory(ApprovalActionType.ASSIGNED,
                    application.getApplicantType(), application.getApplicantId(),
                    LocalDateTime.now(), null, null);
            histories.add(assignmentHistory);
            return;
        }

        //Finding the last approver that's not the same as current reviewer
        ApprovalHistory reassigneeLatestData = new ApprovalHistory();
        Integer index;
        for(index = histories.size()-2; index > -1; index--) {
            ApprovalHistory data = histories.get(index);
            if(data.getApprovalActorType() != application.getCurrentReviewerType() && data.getActorId() != application.getCurrentReviewerId()) {
                reassigneeLatestData = data;
                break;
            }
        }
        application.setCurrentReviewerType(reassigneeLatestData.getApprovalActorType());
        application.setCurrentReviewerId(reassigneeLatestData.getActorId());
    }

    private OfficePost getOfficePost(ApprovalFlowStep currentStep) {
        ReviewerType type = currentStep.getApprover().getType();
        OfficePost officePost;
        switch (type) {
            case POST -> officePost = getOfficePostFromPostType(currentStep.getApprover().getPostTypeId());
            case ROLE -> officePost = getOfficePostFromRole(currentStep.getApprover().getRole(), currentStep.getApprover().getOfficeLayerId());
            default -> throw new RuntimeException("Undefined review type");
        }
        return officePost;
    }

    private OfficePost getOfficePostFromPostType(Integer postTypeId) {
        // TODO: find a way to retrieve post from post type (i.e. Controller of MOCAT)
        return new OfficePost();
    }

    public OfficePost getOfficePostFromRole(String role, Long officeLayerId) {
        // TODO: find a way to retrieve post from role and office layer (i.e. MANAGER of HR department)
        return new OfficePost();
    }

    private ApprovalActorType getReviewActorType(OfficePost officePost) {
        //sending dummy data
        return ApprovalActorType.EMPLOYEE;
    }

    private Long getReviewUserId(OfficePost officePost) {
        //sending dummy data
        return Long.parseLong("5");
    }

    private OfficePost getPostFromActorTypeAndId(ApprovalActorType type, Long id) {
        //sending dummy data
        return new OfficePost();
    }

    private ApprovalFlowStep getNextStep(List<ApprovalFlowStep> steps, OfficePost currentReviewerOfficePost) {
        Integer index = 0;
        for (; index < steps.size(); index++) {
            ApprovalFlowStep step = steps.get(index);
            OfficePost stepOfficePost = getOfficePost(step);
            if(currentReviewerOfficePost.equals(stepOfficePost)) {
                break;
            }
        }

        ApprovalFlowStep nextStep = index >= steps.size() - 1 ? null : steps.get(index + 1);
        return nextStep;
    }
}