# Lab 1 – Implementation Report

## Objective
Implement an end-to-end Camunda 8 loan approval workflow using BPMN, DMN, Java/Spring Boot external job workers, a Camunda REST Connector, process variables, Tasklist and Operate.

## Architecture
- Camunda BPMN coordinates the workflow.
- Java `ApplicantValidationWorker` validates applicant data and calculates financial ratios.
- The Camunda REST Connector calls the Spring Boot mock credit-assessment endpoint.
- The connector stores the HTTP response in `creditCheckResponse`.
- DMN classifies the application as Approved, Rejected or Manual Review.
- Tasklist provides the manual review step.
- Java `LoanOutcomeWorker` records automated and manual-review outcomes.
- Operate is used to inspect instances, variables, failed jobs and incidents.

## Implementation and execution status
The source package contains the workflow models, forms, worker code, mock API and setup/test documentation. Actual deployment and execution results must be entered after testing in the local C8Run environment. Do not claim tests passed until verified in Operate and Tasklist.

## Required test evidence
1. Approved application.
2. Rejected application.
3. Manual-review application and Tasklist completion.
4. REST Connector HTTP 503 failure and recovery.
5. External job worker failure and recovery.

## Limitations
All data is synthetic. The credit API is a local mock and does not contact a credit bureau. The decision thresholds are simplified classroom rules. The REST Connector template must be applied/verified in the installed Camunda Modeler, and its network URL must be reachable from the Connector Runtime.
