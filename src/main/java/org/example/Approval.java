package org.example;

public interface Approval {
    public void initApproval();
    public void approve();
    public void forward();
    public void decline();
    public void requestChange();
}
