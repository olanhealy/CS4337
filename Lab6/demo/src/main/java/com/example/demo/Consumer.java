package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class Consumer {
    private final ObjectMapper objectMapper;
    @Autowired
    public Consumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "export_request", groupId = "export-processing-group")
    public void listenToExportRequest(String message) {
        System.out.println("Received export request: {} " + message);
        processExportRequest(message);
    }
    private void processExportRequest(String message) {
        try {
            Thread.sleep(5000); // Simulate 5 s
            ExportRequest exportRequest = objectMapper.readValue(message, ExportRequest.class);
            System.out.println("Processing export request with exportId: {} " + message);
        } catch (Exception e) {
            System.out.println("Error processing export request " + message);
        }
    }
    public static class ExportRequest {
        private String exportId;
        private String timestamp;
        public String getExportId() {
            return exportId;
        }
        public void setExportId(String exportId) {
            this.exportId = exportId;
        }
        public String getTimestamp() {
            return timestamp;
        }
        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
    }
}

