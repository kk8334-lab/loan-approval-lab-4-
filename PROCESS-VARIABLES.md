# Process-variable dictionary

| Variable | Type | Created by / source | Purpose |
|---|---|---|---|
| applicantName | String | Applicant form | Applicant display name |
| applicantId | String | Applicant form | Synthetic applicant reference |
| email | String | Applicant form | Contact email |
| age | Number | Applicant form | Applicant age |
| monthlyIncome | Number | Applicant form | Monthly income in INR |
| requestedAmount | Number | Applicant form | Requested loan amount in INR |
| existingMonthlyDebt | Number | Applicant form | Existing monthly debt in INR |
| declaredCreditScore | Number | Applicant form | Synthetic score supplied to mock API |
| simulateCreditApiFailure | Boolean | Applicant form | Test-only switch for HTTP 503 |
| applicantValid | Boolean | Java validation worker | Validation result |
| validationMessage | String | Java validation worker | Validation explanation |
| loanToIncomeMultiple | Number | Java validation worker | requestedAmount / monthlyIncome |
| debtToIncomeRatio | Number | Java validation worker | existingMonthlyDebt / monthlyIncome |
| creditCheckResponse | Object | REST Connector | Complete HTTP response wrapper |
| creditCheckResponse.body.creditScore | Number | Mock API response | Credit score consumed by DMN |
| loanDecisionResult | Object | DMN | DMN output containing decision |
| finalStatus | String | BPMN mapping / outcome worker | Approved, Rejected, or Manual Review |
| outcomeRecorded | Boolean | Java outcome worker | Confirms outcome task completed |

## Gateway routing
- `applicantValid = false` → Invalid application end event.
- `applicantValid = true` → REST Connector.
- `loanDecisionResult.decision = "Approved"` → Record approved outcome.
- `loanDecisionResult.decision = "Rejected"` → Record rejected outcome.
- `loanDecisionResult.decision = "Manual Review"` → Tasklist user task assigned to `demo`, then record manual-review outcome.
