package org.example;

import java.util.List;
import java.util.Map;

class LicenseApproval {
    private List<Map<String, ApprovalStep>> licenseApproval;

    // Getters and Setters
    public List<Map<String, ApprovalStep>> getLicenseApproval() {
        return licenseApproval;
    }

    public void setLicenseApproval(List<Map<String, ApprovalStep>> licenseApproval) {
        this.licenseApproval = licenseApproval;
    }
}