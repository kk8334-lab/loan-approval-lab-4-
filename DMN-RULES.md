# DMN rules – Loan Eligibility

- Decision ID: `Decision_LoanEligibility`
- Output: `decision` (String)
- Hit policy: `FIRST`

## Inputs
1. `monthlyIncome`
2. `loanToIncomeMultiple`
3. `creditCheckResponse.body.creditScore`
4. `debtToIncomeRatio`

## Rules

| Priority | Monthly income | Loan-to-income multiple | Credit score | Debt-to-income ratio | Output |
|---|---:|---:|---:|---:|---|
| 1 | < 15000 | - | - | - | Rejected |
| 2 | >= 15000 | > 60 | - | - | Rejected |
| 3 | >= 15000 | <= 60 | < 650 | - | Manual Review |
| 4 | >= 15000 | <= 60 | >= 650 | > 0.40 | Manual Review |
| 5 | >= 15000 | <= 60 | >= 650 | <= 0.40 | Approved |

## Sample evaluations
| Monthly income | Loan amount | Credit score | Existing monthly debt | Expected decision |
|---:|---:|---:|---:|---|
| 60000 | 900000 | 720 | 10000 | Approved |
| 12000 | 300000 | 720 | 1000 | Rejected |
| 50000 | 1000000 | 620 | 10000 | Manual Review |

Calculations:
- Loan-to-income multiple = requested loan amount / monthly income.
- Debt-to-income ratio = existing monthly debt / monthly income.

These are simplified educational rules only. They are not real lending policy or financial advice.
