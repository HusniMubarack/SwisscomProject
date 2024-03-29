package com.swisscom.user.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OpaService {
    private final RestTemplate restTemplate;

    private static final String OPA_URL = "http://127.0.0.1:54183/v1/data/main/allow";

    public OpaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean isAllowed(String action, String role, boolean auth) {
        try {
            // Prepare request body
            String requestBody = "{\"input\": {\"action\": \"" + action
                    + "\", \"user\": {\"authenticated\": " + auth + ", \"role\": \"" + role + "\"}}}";

            // Call OPA service
            ResponseEntity<String> response = restTemplate.postForEntity(OPA_URL, requestBody, String.class);

            // Check OPA decision
            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();
                // Parse response body and extract decision
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(responseBody);
                JsonNode resultNode = jsonNode.get("result");
                if (resultNode != null && resultNode.isBoolean()) {
                    return resultNode.asBoolean();
                } else {
                    // If "result" attribute is missing or not a boolean, deny access
                    return false;
                }
            } else {
                // If OPA service is unavailable, deny access
                return false;
            }
        } catch (Exception e) {
            // Log and handle any exceptions
            System.err.println("Error occurred while communicating with OPA: " + e.getMessage());
            return false;
        }
    }

}
