package com.dsi.approvalflow;

import com.dsi.approvalflow.mockentity.Application;
import com.dsi.approvalflow.mockentity.ApplicationType;
import com.dsi.approvalflow.mockentity.User;

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
