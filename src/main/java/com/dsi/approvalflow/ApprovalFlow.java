package com.dsi.approvalflow;

import java.util.List;
import java.util.Map;

public interface ApprovalFlow {
    void submit();
    void sendBack(String comment, boolean toApplicant);
    void forward(String comment);
    void approve(String comment);
    void reject(String comment);
    void sendBackForCorrection(String comment, boolean toApplicant, List<Map<String, String>> correctableFields);
}
