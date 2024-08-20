package com.dsi.approvalflow.mockentity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class LeaveApplication extends Application<Integer> {
    private int employeeId;
    private LocalDate fromDate;
    private LocalDate tillDate;
    private String reason;
}
