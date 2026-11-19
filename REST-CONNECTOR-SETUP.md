# REST Connector setup – Lab 1

## Purpose
The credit-assessment HTTP request must be executed by the **Camunda REST Connector**. Do not replace it with Java `RestClient`, `WebClient`, `RestTemplate`, a custom URL-calling worker, or a plain Service Task without the REST Connector template.

The BPMN element remains a Service Task, but apply the **REST Connector** template in Camunda Modeler to the task named `Check credit assessment (REST Connector)`.

## 1. Start the local environment
1. Start C8Run.
2. Start the Spring Boot project from the `worker/` folder using `mvn spring-boot:run`.
3. This one Spring Boot application hosts the Java external job workers and the mock API.
4. Verify the API from Windows/Postman before configuring the connector:
   `POST http://localhost:8081/api/credit-assessment`

## 2. Apply the connector template
1. Open `bpmn/loan-approval.bpmn` in Camunda Modeler.
2. Select the Service Task named `Check credit assessment (REST Connector)`.
3. In the Properties panel, open **Template** / template chooser.
4. Search for and apply **REST Connector** (or the equivalent REST outbound connector template installed with your C8Run/Modeler version).
5. Confirm that connector-specific HTTP configuration fields appear.
6. Save the BPMN file. If the template is not available, check that the connector templates and Connector Runtime are installed/enabled for this environment.

## 3. Configure the connector fields
Use the matching fields in your installed template. Field labels may differ slightly by template version.

| Field | Value |
|---|---|
| Authentication | None |
| HTTP method | POST |
| URL – local connector runtime on same Windows host | `http://localhost:8081/api/credit-assessment` |
| URL – connector runtime in Docker, API on Windows host | `http://host.docker.internal:8081/api/credit-assessment` |
| Query parameters | Empty |
| HTTP headers | FEEL context below |
| Request body | FEEL context below |
| Response format | As JSON |
| Result variable | `creditCheckResponse` |
| Result expression | Leave empty for this first implementation |

HTTP Headers FEEL expression:
```feel
={
  "Content-Type": "application/json",
  "Accept": "application/json"
}
```

Request body FEEL expression:
```feel
={
  "applicantId": applicantId,
  "applicantName": applicantName,
  "monthlyIncome": monthlyIncome,
  "requestedAmount": requestedAmount,
  "existingMonthlyDebt": existingMonthlyDebt,
  "declaredCreditScore": declaredCreditScore,
  "simulateFailure": simulateCreditApiFailure
}
```

Important:
- Keep the leading `=` because these fields are FEEL expressions.
- JSON property names are quoted; process-variable references are not quoted.
- Numeric variables must remain numeric, not quoted strings.
- Names are case-sensitive.
- The complete response is stored in `creditCheckResponse`. Its body fields are accessed using `creditCheckResponse.body.<field>`.
- Do not create process variables named `body`, `url`, `method`, `headers`, `authentication`, `queryParameters`, `connectionTimeoutInSeconds`, or `readTimeoutInSeconds`; these are reserved by the REST Connector.

## 4. Verify the process/DMN mapping
The DMN input for credit score is:
```feel
creditCheckResponse.body.creditScore
```
The gateway after the DMN reads:
```feel
loanDecisionResult.decision
```
with exact outcomes `Approved`, `Rejected`, and `Manual Review`.

## 5. Network address selection
Use the URL reachable from the Connector Runtime, not simply the URL that works in the browser:
- C8Run connector runtime running as a local Windows process: start with `localhost`.
- Connector runtime running inside Docker while the API runs on Windows: use `host.docker.internal`.
- If neither works, inspect the Connector Runtime logs and test the host/port from its network context. Do not guess a different hostname.

## 6. Failure and recovery
1. Start a process with `simulateCreditApiFailure = true`.
2. The mock API returns HTTP 503.
3. Inspect the connector task and incident/error details in Operate.
4. Set the mock form variable back to false for a new instance, or restore the service and retry the failed job where the connector/runtime supports retry.
5. Record what happened in `docs/TEST-CASES.csv`.

## 7. Important distinction
The Java `validate-applicant` and `record-loan-outcome` tasks are external job workers. They do not call the credit API. The credit API call is exclusively handled by the REST Connector.
