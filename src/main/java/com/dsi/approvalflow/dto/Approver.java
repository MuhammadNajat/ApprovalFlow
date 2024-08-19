package com.dsi.approvalflow.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Approver {
    private Long id;
    private String type;
    private String postId;
    private Long officeLayerId;
    private String role;
}
