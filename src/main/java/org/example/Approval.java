package org.example;

public interface Approval {
    public void submit();
    public void sendBackToApplicant(boolean toApplicant);
    public void forward();
    public void approve();
    public void reject();
}
