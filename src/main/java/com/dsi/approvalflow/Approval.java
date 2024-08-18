package com.dsi.approvalflow;

public interface Approval {
    public void submit(Object approvalRequest);
    public void sendBack(Object approvalRequest, boolean toApplicant, String comment);
    public void forward(Object approvalRequest, Object userProfile, String comment);
    public void approve(Object approvalRequest, String comment);
    public void reject(Object approvalRequest, String comment);
}
