package com.dsi.approvalflow;

import java.util.List;
import java.util.Map;

public interface Approval {
    public void submit();
    public void sendBack(String comment, boolean toApplicant);
    public void forward(String comment);
    public void approve(String comment);
    public void reject(String comment);
    public void sendBackForCorrection(String comment, boolean toApplicant, List<Map<String, String>> correctableFields);
}
