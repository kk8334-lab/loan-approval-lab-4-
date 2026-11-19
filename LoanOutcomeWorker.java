package edu.lab.loan;

import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LoanOutcomeWorker {

    @JobWorker(type = "record-loan-outcome")
    public Map<String, Object> record(ActivatedJob job) {
        Object status = job.getVariablesAsMap().getOrDefault("finalStatus", "Unknown");
        Map<String, Object> result = new HashMap<>();
        result.put("finalStatus", status);
        result.put("outcomeRecorded", true);
        return result;
    }
}
