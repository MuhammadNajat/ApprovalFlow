package com.dsi.approvalflow.dto.approval;

import com.dsi.approvalflow.dto.Approver;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class ApprovalFlowStep {
    private Approver approver;
    private List<ReviewAction> allowedActions;

    // TODO: find out if it is really required
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApprovalFlowStep that = (ApprovalFlowStep) o;
        return Objects.equals(approver, that.approver)
                && Objects.equals(this.allowedActions, that.allowedActions);
    }
}
