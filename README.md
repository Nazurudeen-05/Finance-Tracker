# Finance Tracker

A personal finance tracker built with Java and Spring Boot. Track income and expenses, import bank CSVs, auto-categorize transactions, and see monthly reports.

## Status
Early development. Currently supports creating, listing, and deleting transactions through a REST API.

## Tech stack
- Java 17, Spring Boot 3
- Spring Data JPA with H2 (file-based)
- JUnit 5
- GitHub Actions CI

## Run it
```
mvn spring-boot:run
```
API base: `http://localhost:8080/api/transactions`

Example:
```
curl -X POST localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{"date":"2026-11-01","description":"Coffee","amount":-4.50,"category":"Food"}'
```

## Roadmap
- [ ] CSV import with duplicate detection
- [ ] Rule-based auto-categorization
- [ ] Monthly reports by category
- [ ] Budgets and overspending alerts
- [ ] Authentication (Spring Security)
- [ ] Dockerfile and OpenAPI docs
