# Credit Card Management System (CCMS)

A Spring Boot microservices project for managing customers, credit cards, merchants, transactions, authentication, and reports.

## Services

| Service                    | Port |
| -------------------------- | ---: |
| API Gateway                | 8080 |
| Customer Card Service      | 8081 |
| Merchant Service           | 8082 |
| Transaction Report Service | 8083 |
| Auth Service               | 8084 |

## Prerequisites

* JDK used by the project
* Oracle Database / Oracle XE
* SQL Developer
* Spring Tool Suite or another Java IDE
* Git

## Database Setup

1. Create a new empty Oracle schema.
2. Run this file from SQL Developer:

```text
database/01-ccms-full-demo-setup.sql
```

This creates all CCMS tables and imports the demo data.

Do not run this script on a schema that already contains CCMS tables or data.

## Application Configuration

For every service:

1. Go to `src/main/resources`.
2. Copy `application-example.properties`.
3. Rename the copy to `application.properties`.
4. Set your local Oracle JDBC URL, username, and password.
5. Set the same Base64 JWT secret in every service.

Never commit `application.properties`, passwords, JWT secrets, or tokens.

## Run Order

Start the services in this order:

1. Auth Service — port 8084
2. Customer Card Service — port 8081
3. Merchant Service — port 8082
4. Transaction Report Service — port 8083
5. API Gateway — port 8080

Use the Gateway for final API testing:

```text
http://localhost:8080
```

## Team Git Rules

* Work in your own Git branch.
* Commit Java, SQL, documentation, and test changes.
* For every database structure change, add a new numbered SQL file in `database/`.
* Do not upload passwords, JWT secrets, tokens, or real/private data.
* Pull the latest `main` branch before starting new work.
