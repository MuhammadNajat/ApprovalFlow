package com.dsi.approvalflow.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Approver {
    private String type;
    private String postId;
    private Integer officeLayerId;
    private String role;
}
