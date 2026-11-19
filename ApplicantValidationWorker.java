package edu.lab.loan;

import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ApplicantValidationWorker {

    @JobWorker(type = "validate-applicant")
    public Map<String, Object> validate(ActivatedJob job) {
        Map<String, Object> v = job.getVariablesAsMap();

        String name = stringValue(v.get("applicantName"));
        String applicantId = stringValue(v.get("applicantId"));
        String email = stringValue(v.get("email"));

        Number age = numberValue(v.get("age"));
        Number income = numberValue(v.get("monthlyIncome"));
        Number amount = numberValue(v.get("requestedAmount"));
        Number debt = numberValue(v.get("existingMonthlyDebt"));
        Number score = numberValue(v.get("declaredCreditScore"));

        boolean valid =
            !name.isBlank() &&
            !applicantId.isBlank() &&
            email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$") &&
            age != null && age.intValue() >= 18 && age.intValue() <= 70 &&
            income != null && income.doubleValue() > 0 &&
            amount != null && amount.doubleValue() > 0 &&
            debt != null && debt.doubleValue() >= 0 &&
            score != null && score.intValue() >= 300 && score.intValue() <= 900;

        double monthlyIncome = income == null ? 0 : income.doubleValue();
        double requestedAmount = amount == null ? 0 : amount.doubleValue();
        double existingDebt = debt == null ? 0 : debt.doubleValue();

        Map<String, Object> result = new HashMap<>();
        result.put("applicantValid", valid);
        result.put("validationMessage", valid
            ? "Applicant information validated"
            : "One or more applicant fields are missing or invalid");
        result.put("loanToIncomeMultiple",
            monthlyIncome > 0 ? requestedAmount / monthlyIncome : 0.0);
        result.put("debtToIncomeRatio",
            monthlyIncome > 0 ? existingDebt / monthlyIncome : 1.0);

        return result;
    }

    private String stringValue(Object value) {
        return value == null ? "" : value.toString().trim();
    }

    private Number numberValue(Object value) {
        return value instanceof Number ? (Number) value : null;
    }
}
