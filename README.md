# Lab 1 – End-to-End Loan Approval Automation

## Objective
Implement an end-to-end loan approval workflow in Camunda 8 using BPMN orchestration, a DMN decision table, Java/Spring Boot external job workers, the Camunda REST Connector, process variables, Tasklist human review, and Operate monitoring.

## Workflow
1. The applicant submits synthetic application details through the start form.
2. The `validate-applicant` Java external job worker validates the required fields and calculates the loan-to-income multiple and debt-to-income ratio.
3. An exclusive gateway ends invalid applications.
4. A BPMN Service Task configured with the **Camunda REST Connector template** sends the valid application to the Spring Boot mock credit-assessment API.
5. The connector stores the complete HTTP response in `creditCheckResponse`.
6. The DMN decision evaluates income, loan-to-income multiple, credit score returned by the API, and debt-to-income ratio.
7. An exclusive gateway routes Approved, Rejected, and Manual Review outcomes.
8. Manual Review is assigned to the `demo` user in Tasklist.
9. Java workers record the final outcome for the automated branches and after manual review.

## Technology
- Camunda 8 / C8Run
- Camunda Modeler
- BPMN 2.0 and DMN
- Camunda REST Connector (`io.camunda:http-json:1`)
- Java 17+, Spring Boot, Maven
- Mock REST API hosted by the Spring Boot application
- Tasklist and Operate

## Folder structure
```text
Lab1_Loan_Approval_REST_Connector/
├── README.md
├── bpmn/loan-approval.bpmn
├── dmn/loan-eligibility.dmn
├── forms/loan-application.form
├── worker/
│   ├── pom.xml
│   └── src/main/
│       ├── java/edu/lab/loan/
│       │   ├── LoanApprovalWorkerApplication.java
│       │   ├── ApplicantValidationWorker.java
│       │   ├── LoanOutcomeWorker.java
│       │   └── CreditAssessmentController.java
│       └── resources/application.yaml
├── docs/
│   ├── REST-CONNECTOR-SETUP.md
│   ├── API-EXAMPLES.md
│   ├── PROCESS-VARIABLES.md
│   ├── DMN-RULES.md
│   ├── TEST-CASES.csv
│   └── IMPLEMENTATION-REPORT.md
└── screenshots/
```

## Run order
1. Start C8Run and confirm Operate and Tasklist are available.
2. From `worker/`, run `mvn clean package`.
3. Start the Spring Boot application using `mvn spring-boot:run`. It hosts both the external job workers and the mock credit API on port 8081.
4. Test `POST http://localhost:8081/api/credit-assessment` using the sample request in `docs/API-EXAMPLES.md`.
5. Open `bpmn/loan-approval.bpmn` in Camunda Modeler. Select the credit assessment Service Task and apply the **REST Connector** template. Configure it exactly as described in `docs/REST-CONNECTOR-SETUP.md`, then save.
6. Deploy the BPMN, DMN, and form resources to the local C8Run cluster.
7. Run the test cases and inspect Tasklist and Operate.

## Important connector networking note
Use an endpoint address that the Connector Runtime can reach:
- If the connector runtime runs directly on the same Windows host as the Spring Boot application, use `http://localhost:8081/api/credit-assessment`.
- If the connector runtime runs in Docker and Spring Boot runs on Windows, use `http://host.docker.internal:8081/api/credit-assessment`.
Do not assume the browser's `localhost` is reachable from a container. Test the endpoint from the connector runtime's network context.

## Test evidence
The test-case table contains expected routes. Fill in Actual Result and Status only after running each scenario. Capture genuine screenshots from your own Camunda environment in `screenshots/`; no screenshots are fabricated or included.

## Safety and limitations
All applicant data and credit scores are synthetic. The API is a classroom mock and does not contact a credit bureau. The DMN thresholds are simplified educational rules, not real lending policy. Never commit secrets, real identity information, or real financial credentials.
