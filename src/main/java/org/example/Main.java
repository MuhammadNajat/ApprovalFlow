package org.example;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        ApprovalDataParser approvalDataParser = new ApprovalDataParser();
        approvalDataParser.parseJSONFile("/home/najat/Development/0__Project__Scratch/project-scratch/src/main/webapp/resources/ApprovalPath/leaveApproval.json");
        System.out.println("Hello world!");
    }
}