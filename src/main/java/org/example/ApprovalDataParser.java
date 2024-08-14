package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ApprovalDataParser {
    public void parseJSONFile(String path) throws ParserConfigurationException, IOException, SAXException {
        File jsonFile = new File(path);

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Map<String, Object> keyValueMap = objectMapper.readValue(jsonFile, new TypeReference< Map<String, Object> >() {});

            for (Map.Entry<String, Object> entry : keyValueMap.entrySet()) {
                System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
