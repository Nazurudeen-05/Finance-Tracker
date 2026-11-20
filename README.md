# Finance Tracker

A personal finance tracker built with Java and Spring Boot. It records income and expenses, shows spending by category, and is being extended with merged bank accounts, spending limits with alerts, bill reminders, expense splitting, receipt uploads, forecasting and multi-user support.

## Status

| Feature | Status |
|---|---|
| Transaction REST API (create, list, delete) | Done |
| Summary endpoint (income, expenses, balance, spending by category) | Done |
| Web UI: dashboard, transactions table, reports, about | Done |
| Sign-in screen (demo only, no server-side checks yet) | Done |
| Multiple bank accounts with a merged overview | Planned |
| CSV import per account with duplicate detection | Planned |
| Spending limits with alerts | Planned |
| Authentication, roles and multi-user support | Planned |
| Bill reminders | Planned |
| Split expenses | Planned |
| Receipt upload | Planned |
| Spending forecast | Planned |

Only the items marked Done exist in the code today. The sections below describe how the planned features will be built.

## Tech stack

- Java 17+, Spring Boot 3, Spring Data JPA
- H2 database (file based)
- JUnit 5 and Mockito
- Plain HTML, CSS and JavaScript front end (no build step)
- GitHub Actions CI

## Run it

```
mvn spring-boot:run
```

Open `http://localhost:8080`. Run the tests with `mvn test`.

## Current API

| Method | Path | Purpose |
|---|---|---|
| GET | `/api/transactions` | List transactions, newest first |
| POST | `/api/transactions` | Add a transaction |
| DELETE | `/api/transactions/{id}` | Delete a transaction |
| GET | `/api/transactions/summary` | Totals and expenses by category |

## Planned: merging bank accounts

A user adds several accounts (checking, savings, credit card) and sees one combined balance and transaction list, with an account filter.

- **Model:** `Account` (`id`, `name`, `type`, `currency`, `openingBalance`). `Transaction` gets an `accountId`.
- **Endpoints:** `POST /api/accounts`, `GET /api/accounts`, `GET /api/accounts/overview`, `POST /api/accounts/{id}/import`.
- **Merge rules:** imported rows become transactions linked to their account. Duplicates are skipped by matching account, date, description and amount. The overview sums balances across accounts.
- **Build order:** `Account` entity and service with tests, then `accountId` on transactions, then CSV import, then the overview endpoint and an Accounts tab.

## Planned: spending limits and alerts

The user sets a monthly limit, overall or per category, and is warned as spending approaches and passes it.

- **Model:** `SpendingLimit` (`id`, `category`, `monthlyAmount`, `warnAtPercent`, default 80).
- **Endpoints:** `POST /api/limits`, `GET /api/limits`, `GET /api/limits/status`, `GET /api/alerts`.
- **States for the current month:** `OK` below the threshold, `WARNING` at the threshold, `EXCEEDED` at 100% or more.
- **Delivery:** limits are re-checked whenever a transaction is added. An alert is created once per state change per month and shown as a banner and badge in the UI.
- **Tests first:** exact threshold, over the limit, income ignored, only the current month counts, month rollover resets state.

## Planned: larger features

These depend on the earlier work. Authentication should come before anything that stores data per person.

### Authentication, roles and multi-user support

Each person sees only their own accounts, transactions and limits.

- **Model:** `AppUser` (`id`, `email`, `passwordHash`, `role`). Every owned entity gets a `userId`.
- **Approach:** Spring Security with BCrypt password hashing and roles `USER` and `ADMIN`. Repository queries always filter by the signed-in user, and the front end sign-in screen is switched to call the real endpoint.
- **Endpoints:** `POST /api/auth/register`, `POST /api/auth/login`, `GET /api/auth/me`.
- **Tests:** a user cannot read or delete another user's transactions, unauthenticated requests are rejected, and passwords are never returned or stored in plain text.

### Bill reminders

Track upcoming bills and notify the user before they are due.

- **Model:** `Bill` (`id`, `name`, `amount`, `dueDay`, `frequency`, `remindDaysBefore`, `lastRemindedOn`, `paid`).
- **Endpoints:** `POST /api/bills`, `GET /api/bills`, `PATCH /api/bills/{id}/paid`, `DELETE /api/bills/{id}`.
- **Scheduling:** a `@Scheduled` job runs daily, finds bills due within `remindDaysBefore` days, and sends one reminder per due date (checked with `lastRemindedOn`) through Spring Mail. The UI also lists upcoming bills on the Dashboard.
- **Tests:** a reminder is sent once, a paid bill is skipped, and month-end due dates (for example the 31st) are handled.

### Split expenses

Share a cost with other people and track who owes what.

- **Model:** `ExpenseGroup`, `Member`, `SharedExpense` (`payerId`, `amount`, `splitType`), and `Share` (`memberId`, `amount`).
- **Endpoints:** `POST /api/groups`, `POST /api/groups/{id}/expenses`, `GET /api/groups/{id}/balances`, `POST /api/groups/{id}/settle`.
- **Rules:** supports equal and custom splits, and shares must add up to the total amount. Balances show each member's net position, and settle-up suggests the fewest payments needed.
- **Tests:** rounding is handled so shares always sum to the total, and balances across a group sum to zero.

### Receipt upload

Attach a photo or PDF to a transaction.

- **Model:** `Receipt` (`id`, `transactionId`, `storedName`, `contentType`, `size`).
- **Endpoints:** `POST /api/transactions/{id}/receipt` (multipart), `GET /api/transactions/{id}/receipt`, `DELETE /api/transactions/{id}/receipt`.
- **Safety rules:** allow only JPEG, PNG and PDF, set a size limit, store files under a generated name rather than the uploaded name, and check that the transaction belongs to the signed-in user.
- **Tests:** wrong file types and oversized files are rejected, and path tricks in file names cannot escape the storage folder.

### Spending forecast

Estimate the month-end balance from recent patterns.

- **Approach:** start with a simple, explainable method. Project remaining spending from the average daily spend over the last 30 days, add known recurring items and unpaid bills, and subtract from the current balance.
- **Endpoint:** `GET /api/forecast` returns the projected month-end balance, the daily average used, and the items included, so the result can be explained in the UI.
- **Tests:** an empty history gives a clear "not enough data" result, recurring items are counted once, and the calculation uses a fixed clock so tests are repeatable.

## Suggested build order

1. Accounts and merged overview
2. CSV import with duplicate detection
3. Spending limits and alerts
4. Authentication and roles
5. Bill reminders
6. Receipt upload
7. Split expenses
8. Spending forecast

## Roadmap

- [x] Transaction API and summary endpoint
- [x] Web UI with dashboard, transactions, reports and about tabs
- [ ] Accounts and merged overview
- [ ] CSV import with duplicate detection
- [ ] Spending limits with alerts
- [ ] Spring Security authentication, roles and multi-user support
- [ ] Bill reminders
- [ ] Receipt upload
- [ ] Split expenses
- [ ] Spending forecast
- [ ] Rule-based auto-categorization
- [ ] Dockerfile and OpenAPI documentation
