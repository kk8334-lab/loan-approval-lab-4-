package edu.lab.loan;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CreditAssessmentController {

    @PostMapping("/credit-assessment")
    public ResponseEntity<Map<String, Object>> assess(
            @RequestBody Map<String, Object> request) {

        boolean simulateFailure =
            Boolean.TRUE.equals(request.get("simulateFailure"));

        if (simulateFailure) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", "Simulated credit assessment service outage");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
        }

        String applicantId = String.valueOf(
            request.getOrDefault("applicantId", "SYNTHETIC-APPLICANT"));
        String applicantName = String.valueOf(
            request.getOrDefault("applicantName", "Synthetic Applicant"));

        int creditScore = 700;
        Object scoreValue = request.get("declaredCreditScore");
        if (scoreValue instanceof Number) {
            creditScore = ((Number) scoreValue).intValue();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("applicantId", applicantId);
        response.put("applicantName", applicantName);
        response.put("creditScore", creditScore);
        response.put("assessmentReference", "CREDIT-SIM-001");
        response.put("message", "Synthetic credit assessment completed");

        return ResponseEntity.ok(response);
    }
}
