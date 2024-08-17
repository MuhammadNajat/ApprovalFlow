package com.dsi.approvalflow;

import com.dsi.approvalflow.dto.ApprovalPaths;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class ApprovalDataParser {
    static ApprovalPaths approvalPaths;

    public static void parseData(String configPath) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            approvalPaths = objectMapper.readValue(new File(configPath), ApprovalPaths.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ApprovalPaths getApprovalPaths() {
        return approvalPaths;
    }
}