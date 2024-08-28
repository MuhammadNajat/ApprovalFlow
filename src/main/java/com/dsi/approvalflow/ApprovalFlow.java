package com.dsi.approvalflow;

import java.util.List;
import java.util.Map;

public interface ApprovalFlow {
    void submit();
    void resubmit();
    void sendBack(String comment, boolean toApplicant, List<Map<String, String>> correctableFields);
    void forward(String comment);
    void approve(String comment);
    void reject(String comment);
}
