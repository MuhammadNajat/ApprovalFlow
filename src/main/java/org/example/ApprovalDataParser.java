package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;

public class ApprovalDataParser {
    public void parseJSONFile(String path) throws ParserConfigurationException, IOException, SAXException {
        try {
            // Read the JSON file content as a String
            String jsonString = new String(Files.readAllBytes(Paths.get(path)));

            // Create a JSONObject from the file content
            JSONObject jsonObject = new JSONObject(jsonString);

            // Extract and print key-value pairs
            extractKeyValuePairs(jsonObject, "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void parseData(String path) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Parse JSON file to LicenseApproval object
            LicenseApproval licenseApproval = objectMapper.readValue(new File(path), LicenseApproval.class);

            // Process the parsed data
            for (Map<String, ApprovalStep> step : licenseApproval.getLicenseApproval()) {
                for (Map.Entry<String, ApprovalStep> entry : step.entrySet()) {
                    System.out.println("Step: " + entry.getKey());
                    System.out.println("Approver Post ID: " + entry.getValue().getApproverPostId());
                    System.out.println("Allowed Actions: " + entry.getValue().getAllowedActions());
                    System.out.println("-----------------------");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void extractKeyValuePairs(JSONObject jsonObject, String parentKey) {
        Iterator<String> keys = jsonObject.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            String newKey = parentKey.isEmpty() ? key : parentKey + "." + key;
            Object value = jsonObject.get(key);

            if (value instanceof JSONObject) {
                extractKeyValuePairs((JSONObject) value, newKey);
            } else if (value instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) value;
                for (int i = 0; i < jsonArray.length(); i++) {
                    extractKeyValuePairs(jsonArray.getJSONObject(i), newKey + "[" + i + "]");
                }
            } else {
                System.out.println(newKey + " : " + value);
            }
        }
    }
}
