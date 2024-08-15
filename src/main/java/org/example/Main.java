package org.example;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        ApprovalDataParser approvalDataParser = new ApprovalDataParser();
        approvalDataParser.parseData("/home/najat/Documents/ApprovalFlow/src/main/resources/approvalFlow.json");
        System.out.println("Parsing done");
    }
}