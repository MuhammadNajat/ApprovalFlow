package com.dsi.approvalflow;

import com.dsi.approvalflow.dto.ApprovalFlowStep;;
import com.dsi.approvalflow.mockentity.*;
import com.dsi.approvalflow.util.ApprovalDataManager;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class LeaveApproval implements Approval {
    private ApprovalRequest approvalRequest;
    private List<ApprovalFlowStep> steps;
    private String configPath = "/home/najat/Documents/ApprovalFlow/src/main/resources/approvalFlow.json";
    private List<ApprovalHistory> history = new ArrayList<>();

    public LeaveApproval(String applicationType, ApprovalRequest approvalRequest) {
        steps = ApprovalDataManager.getApprovalFlowSteps(configPath, applicationType);
        this.approvalRequest = approvalRequest;
    }

    @Override
    public void submit() {
        ApprovalFlowStep step = steps.get(0);
        Post post = getPost(step);

        approvalRequest.setState(ApprovalState.IN_REVIEW);

        ApprovalActorType actorType = getReviewActorType(post);
        approvalRequest.setCurrentReviewerType(actorType);

        Long reviewerId = getReviewUserId(post);
        approvalRequest.setCurrentReviewerId(reviewerId);

        ApprovalHistory record = new ApprovalHistory(ApprovalActionType.GOT_ASSIGNED,
                actorType, reviewerId, LocalDateTime.now(), null, null);
        history.add(record);
    }

    @Override
    public void sendBack(String comment, boolean toApplicant) {
        approvalRequest.setState(ApprovalState.IN_REVIEW);
        returnApplication(comment, toApplicant, ApprovalActionType.RETURN, null);
    }

    @Override
    public void forward(String comment) {
        ApprovalHistory forwardHistory = new ApprovalHistory(ApprovalActionType.FORWARD, approvalRequest.getCurrentReviewerType(),
                approvalRequest.getCurrentReviewerId(), LocalDateTime.now(), null, comment);
        history.add(forwardHistory);

        Post currentApproverPost = getPostFromActorTypeAndId(approvalRequest.getCurrentReviewerType(),
                approvalRequest.getCurrentReviewerId());
        ApprovalFlowStep nextStep = getNextStep(steps, currentApproverPost);
        Post post = getPost(nextStep);

        approvalRequest.setState(ApprovalState.IN_REVIEW);

        ApprovalActorType actorType = getReviewActorType(post);
        approvalRequest.setCurrentReviewerType(actorType);

        Long reviewerId = getReviewUserId(post);
        approvalRequest.setCurrentReviewerId(reviewerId);

        ApprovalHistory assignmentHistory = new ApprovalHistory(ApprovalActionType.GOT_ASSIGNED, actorType,
                reviewerId, LocalDateTime.now(), null, comment);
        history.add(assignmentHistory);
    }

    @Override
    public void approve(String comment) {
        approvalRequest.setState(ApprovalState.APPROVED);
        ApprovalHistory approveHistory = new ApprovalHistory(ApprovalActionType.APPROVE, approvalRequest.getCurrentReviewerType(),
                approvalRequest.getCurrentReviewerId(), LocalDateTime.now(), null, comment);
    }

    @Override
    public void reject(String comment) {
        approvalRequest.setState(ApprovalState.REJECTED);
        ApprovalHistory approveHistory = new ApprovalHistory(ApprovalActionType.REJECT, approvalRequest.getCurrentReviewerType(),
                approvalRequest.getCurrentReviewerId(), LocalDateTime.now(), null, comment);
    }

    @Override
    public void sendBackForCorrection(String comment, boolean toApplicant, List<Map<String, String>> correctableFields) {
        approvalRequest.setState(ApprovalState.IN_REVIEW);
        returnApplication(comment, toApplicant, ApprovalActionType.REQUEST_CORRECTIONS, correctableFields);
    }

    private void returnApplication(String comment, boolean toApplicant, ApprovalActionType actionType, List<Map<String, String>> correctableFields) {
        if(history.size() == 0) {
            return;
        }

        ApprovalHistory sendBackHistory = new ApprovalHistory(actionType,
                approvalRequest.getCurrentReviewerType(), approvalRequest.getCurrentReviewerId(),
                LocalDateTime.now(), null, comment);
        history.add(sendBackHistory);

        approvalRequest.setState(ApprovalState.RETURNED);

        if(history.size() == 1 || toApplicant) {
            approvalRequest.setCurrentReviewerType(approvalRequest.getApplicantType());
            approvalRequest.setCurrentReviewerId(approvalRequest.getApplicantId());

            ApprovalHistory assignmentHistory = new ApprovalHistory(ApprovalActionType.GOT_ASSIGNED,
                    approvalRequest.getApplicantType(), approvalRequest.getApplicantId(),
                    LocalDateTime.now(), null, null);
            history.add(assignmentHistory);
            return;
        }

        //Finding the last approver that's not the same as current reviewer
        ApprovalHistory reassigneeLatestData = new ApprovalHistory();
        Integer index;
        for(index = history.size()-2; index > -1; index--) {
            ApprovalHistory data = history.get(index);
            if(data.getApprovalActorType() != approvalRequest.getCurrentReviewerType() && data.getActorId() != approvalRequest.getCurrentReviewerId()) {
                reassigneeLatestData = data;
                break;
            }
        }
        approvalRequest.setCurrentReviewerType(reassigneeLatestData.getApprovalActorType());
        approvalRequest.setCurrentReviewerId(reassigneeLatestData.getActorId());
    }

    private Post getPost(ApprovalFlowStep step) {
        String type = step.getApprover().getType();
        Post post = new Post();
        if (type.equals("POST")) {
            post = new Post();
        }
        else if(type == "ROLE") {
            post = getPostForRole(step.getApprover().getRole(), step.getApprover().getOfficeLayerId());
        }
        return post;
    }

    public Post getPostForRole(String role, Long officeLayerId) {
        //sending dummy data
        return new Post();
    }

    private ApprovalActorType getReviewActorType(Post post) {
        //sending dummy data
        return ApprovalActorType.EMPLOYEE;
    }

    private Long getReviewUserId(Post post) {
        //sending dummy data
        return Long.parseLong("5");
    }

    private Post getPostFromActorTypeAndId(ApprovalActorType type, Long id) {
        //sending dummy data
        return new Post();
    }

    private ApprovalFlowStep getNextStep(List<ApprovalFlowStep> steps, Post currentReviewerPost) {
        Integer index = 0;
        for (; index < steps.size(); index++) {
            ApprovalFlowStep step = steps.get(index);
            Post stepPost = getPost(step);
            if(currentReviewerPost.equals(stepPost)) {
                break;
            }
        }

        ApprovalFlowStep nextStep = index >= steps.size() - 1 ? null : steps.get(index + 1);
        return nextStep;
    }
}