package com.dsi.approvalflow;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        LeaveApproval leaveApproval = new LeaveApproval(
                "/home/najat/Documents/ApprovalFlow/src/main/resources/approvalFlow.json",
                "LEAVE_APPLICATION"
        );

        leaveApproval.submit();
        leaveApproval.forward("Okay for me");
        leaveApproval.sendBack("Check carefully", false);
        leaveApproval.forward("Seems good");
        leaveApproval.sendBackForCorrection("Sending back", true, null);
        leaveApproval.forward("Corrected.");
        leaveApproval.forward("Checked. 1");
        leaveApproval.forward("Checked. 2");
        leaveApproval.forward("Checked. 3");
        leaveApproval.approve("Approved. Thanks.");
    }
}