package com.dsi.approvalflow.dto;

public class Approver {
    private String type;
    private String postId;
    private Integer officeLayerId;
    private String role;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public Integer getOfficeLayerId() {
        return officeLayerId;
    }

    public void setOfficeLayerId(Integer officeLayerId) {
        this.officeLayerId = officeLayerId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
