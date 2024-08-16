package com.dsi.approvalflow;

public interface Approval {
    public void submit();
    public void sendBack(boolean toApplicant);
    public void forward();
    public void approve();
    public void reject();
}
