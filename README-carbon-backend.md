# CarbonLedger --- Backend

Java + Spring Boot REST API for the CarbonLedger carbon-accounting
prototype, with MySQL persistence and Spring Security/JWT
authentication.

## Project links

-   **Backend repository:**
    https://github.com/Ramchandra-18/carbon-backend
-   **Frontend repository:**
    https://github.com/Ramchandra-18/carbon-frontend
-   **Live frontend:** https://carbon-frontend-zr8y.vercel.app/
-   **Backend base URL:** https://carbon-backend-xhmi.onrender.com
-   **Swagger UI:**
    https://carbon-backend-xhmi.onrender.com/swagger-ui/index.html
-   **OpenAPI JSON:**
    https://carbon-backend-xhmi.onrender.com/v3/api-docs

## Overview

The backend exposes APIs for authentication, users, organizations,
products, emission factors, activity records, production batches,
emission allocations, and emissions summaries. The deployed OpenAPI
document identifies itself as version 3.1.0.

The assignment requires a working prototype covering Scope 1, Scope 2,
and all 15 Scope 3 category options; factor provenance; product-stage
traceability; backend-enforced role and organization access; and tests
demonstrating calculation and authorization behavior.

**Important:** An endpoint being listed in OpenAPI confirms that it is
documented, not that every acceptance criterion has passed. Fill out the
status/evidence section from actual test runs before submission.

## Technology

-   Java 21
-   Spring Boot
-   Maven
-   Spring Security and JWT
-   MySQL
-   Spring Data JPA / Hibernate, where configured
-   Render deployment
-   Aiven MySQL hosting

## Architecture

``` text
React + Vite frontend
       |
       | HTTPS / REST
       v
Spring Boot REST API
       |
       v
MySQL relational database
```

## API documentation

-   [Swagger
    UI](https://carbon-backend-xhmi.onrender.com/swagger-ui/index.html)
-   [OpenAPI JSON](https://carbon-backend-xhmi.onrender.com/v3/api-docs)

Use Swagger to inspect schemas and try endpoints. Use the OpenAPI JSON
as the machine-readable API specification. The server listed in the
deployed specification is `https://carbon-backend-xhmi.onrender.com`.

## Documented API routes

All paths below are relative to the backend base URL. Consult Swagger
for request/response schemas and actual security requirements.

### Authentication

  Method   Endpoint               Operation
  -------- ---------------------- ------------------
  POST     `/api/auth/register`   Register
  POST     `/api/auth/login`      Login
  GET      `/api/auth/me`         Get current user

The OpenAPI `LoginResponse` schema contains `accessToken`, `tokenType`,
`expiresIn`, and `user`. The login request contains `email` and
`password`.

For endpoints requiring authentication, pass the returned token as:

``` http
Authorization: Bearer YOUR_ACCESS_TOKEN
```

### Users

  Method   Endpoint                       Operation
  -------- ------------------------------ --------------------
  GET      `/api/users`                   Get all users
  POST     `/api/users`                   Create user
  PATCH    `/api/users/{userId}/status`   Update user status

The documented role enum includes `ADMIN`, `STAKEHOLDER`, and
`REVIEWER`.

### Organizations

  Method   Endpoint                      Operation
  -------- ----------------------------- ----------------------------------
  GET      `/api/organizations`          List organizations
  POST     `/api/organizations`          Create organization
  GET      `/api/public/organizations`   List active public organizations

### Products

  -----------------------------------------------------------------------------------------------
  Method                  Endpoint                                        Operation
  ----------------------- ----------------------------------------------- -----------------------
  POST                    `/api/products`                                 Create product

  GET                     `/api/products/organization/{organizationId}`   List products for an
                                                                          organization
  -----------------------------------------------------------------------------------------------

### Emission factors

  Method   Endpoint                                Operation
  -------- --------------------------------------- -----------------------
  GET      `/api/emission-factors`                 List active factors
  POST     `/api/emission-factors`                 Create factor
  GET      `/api/emission-factors/{id}`            Get factor by ID
  GET      `/api/emission-factors/scope/{scope}`   List factors by scope

The OpenAPI request schema documents `name`, `source`,
`sourceReference`, `activityUnit`, `co2eFactor`, `scope`, optional
`scope3Category`, `validFrom`, and optional `validUntil`.

The assignment additionally requires reproducible factor metadata where
available: source publisher and URL, dataset version, factor year,
geography, unit, gas basis, lifecycle boundary, method, access date, and
license. Verify which of these fields the implementation actually
stores; do not claim fields exist if they are not persisted.

### Activity records

  -------------------------------------------------------------------------------------------------------
  Method                  Endpoint                                                Operation
  ----------------------- ------------------------------------------------------- -----------------------
  POST                    `/api/activity-records`                                 Create activity record

  GET                     `/api/activity-records/organization/{organizationId}`   List organization
                                                                                  activity records

  GET                     `/api/activity-records/all`                             List all activity
                                                                                  records (operation name
                                                                                  indicates admin use)
  -------------------------------------------------------------------------------------------------------

The request schema includes `organizationId`, `emissionFactorId`,
`activityName`, `quantity`, `unit`, `activityDate`, and optional
`description`. The response schema includes `emissionKgCo2e`.

### Production batches

  ------------------------------------------------------------------------------------
  Method                  Endpoint                             Operation
  ----------------------- ------------------------------------ -----------------------
  POST                    `/api/batches`                       Create production batch

  GET                     `/api/batches/product/{productId}`   List batches for a
                                                               product

  GET                     `/api/batches/all`                   List all batches
                                                               (operation name
                                                               indicates admin use)
  ------------------------------------------------------------------------------------

The documented request includes `productId`, `batchCode`,
`productionQuantity`, `productionUnit`, and `productionDate`.

### Emission allocations

  ---------------------------------------------------------------------------------------------
  Method                  Endpoint                                      Operation
  ----------------------- --------------------------------------------- -----------------------
  POST                    `/api/emission-allocations`                   Create allocation

  GET                     `/api/emission-allocations/batch/{batchId}`   List allocations for a
                                                                        batch

  GET                     `/api/emission-allocations/all`               List all allocations
                                                                        (operation name
                                                                        indicates admin use)
  ---------------------------------------------------------------------------------------------

The request includes `activityRecordId`, `batchId`, and
`allocatedEmissionKgCo2e`. The assignment also calls for retaining
allocation numerator, denominator, and percentage and preventing
allocations above 100%; verify and document actual behavior.

### Summary and health

  --------------------------------------------------------------------------------------------------------
  Method                  Endpoint                                                 Operation
  ----------------------- -------------------------------------------------------- -----------------------
  GET                     `/api/emissions-summary/organization/{organizationId}`   Get organization
                                                                                   emissions summary

  GET                     `/api/health`                                            Health check
  --------------------------------------------------------------------------------------------------------

The documented summary response contains `organizationId`,
`scope1Emissions`, `scope2Emissions`, `scope3Emissions`, and
`totalEmissions`.

## Local setup

### Prerequisites

-   JDK 21
-   Git
-   MySQL
-   Maven or the repository's Maven Wrapper

### 1. Clone

``` bash
git clone https://github.com/Ramchandra-18/carbon-backend.git
cd carbon-backend
```

### 2. Configure environment variables

Configure the names expected by the application's
`application.properties`:

  Variable        Purpose
  --------------- --------------------
  `PORT`          HTTP port
  `DB_URL`        JDBC URL
  `DB_USERNAME`   Database username
  `DB_PASSWORD`   Database password
  `JWT_SECRET`    JWT signing secret

Example pattern (confirm it matches the current source configuration):

``` properties
server.port=${PORT:8081}
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/carbon_calculator}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
app.jwt.secret=${JWT_SECRET}
app.jwt.expiration-ms=3600000
```

Do not commit actual secrets or production credentials.

### 3. Run

Windows:

``` bash
.\mvnw.cmd spring-boot:run
```

Linux/macOS:

``` bash
./mvnw spring-boot:run
```

The example default port is `8081`, unless overridden by `PORT`.

### 4. Build and test

Windows:

``` bash
.\mvnw.cmd clean package
.\mvnw.cmd test
```

Linux/macOS:

``` bash
./mvnw clean package
./mvnw test
```

Include actual test output or a test report with the submission.

## Calculation expectations from the assignment

The assignment specifies backend calculation using compatible units:

-   Activity-based calculation:
    `emissions (kg CO2e) = activity quantity × emission factor`, after
    unit conversion.
-   Freight: `tonne-kilometres = shipped mass (tonnes) × distance (km)`,
    then multiply by a compatible factor.
-   Spend-based calculation: match currency and reference year and
    document conversions.
-   Use decimal arithmetic and round only for display.
-   Reject negative quantities, incompatible units, invalid allocation
    ratios, and missing factors rather than treating missing data as
    zero.
-   Snapshot factor selection and calculation inputs so later factor
    catalogue updates do not silently change historical results.
-   Avoid counting the same shipment or emission event more than once in
    a product footprint.
-   Keep organization inventory attribution separate from unique
    emission events allocated to a product/batch.
-   Do not add Scope 2 location-based and market-based alternatives
    together.

These are assignment requirements. Record which are implemented and
tested rather than treating this list as proof of completion.

## Roles and authorization

The assignment defines:

-   **Administrator:** manages users, roles, organizations, factors, and
    category configuration; can inspect all records.
-   **Stakeholder:** creates/edits its own organization's records and
    assigned stage activities; views permitted linked traceability
    summaries.
-   **Reviewer/auditor:** reads permitted reports, factor provenance,
    and audit history without editing.

Authorization must be enforced by backend endpoints and queries, not
only by hiding frontend navigation. Test unauthenticated requests, role
restrictions, cross-organization access denial, and blocked privilege
escalation. Document actual permissions and results.

## Postman and evidence

Include an importable Postman collection and environment with no
secrets. The test report should record the test, expected result, actual
result, pass/fail status, and any limitation.

Minimum assessment evidence to prepare:

-   Calculation tests: unit conversion, factor multiplication, freight
    tonne-kilometres, gas-to-CO2e where applicable, and batch
    allocation.
-   Aggregation tests: shared transport counted once, no supplier/buyer
    double counting, and separate Scope 2 alternatives.
-   Security tests: unauthenticated requests, role restrictions,
    cross-organization denial, and blocked role escalation.
-   Validation tests: negative quantities, invalid allocation,
    incompatible units, and missing factor.
-   Seeded demonstration: one batch with supplier, manufacturer,
    logistics, distributor, wholesaler, and retailer stages, plus
    relevant activity examples.
-   Schema/ER diagram, migrations or schema/seed scripts,
    Swagger/OpenAPI, Postman collection, and application working
    document.

## Implementation status and limitations

Complete this table from actual deployed behavior and test evidence
before submission:

  -------------------------------------------------------------------------------------------------------------
  Acceptance area         Status                                 Evidence / current limitation
  ----------------------- -------------------------------------- ----------------------------------------------
  Scope 1 and Scope 2     `[Pass / Partial / Not implemented]`   `[Test IDs and results]`
  calculation cases                                              

  All 15 Scope 3 category `[Pass / Partial / Not implemented]`   `[Options available; data gaps]`
  options                                                        

  Climatiq references and `[Pass / Partial / Not implemented]`   `[Factor IDs, source URLs, dataset version]`
  reproducible metadata                                          

  Product journey and     `[Pass / Partial / Not implemented]`   `[Seeded batch and reconciliation result]`
  stage totals                                                   

  Allocation and          `[Pass / Partial / Not implemented]`   `[Expected vs actual test]`
  double-count prevention                                        

  Role-based and          `[Pass / Partial / Not tested]`        `[Request/result evidence]`
  cross-organization                                             
  authorization                                                  

  Postman collection and  `[Complete / Partial / Missing]`       `[File path/link]`
  test report                                                    

  ER diagram, schema, and `[Complete / Partial / Missing]`       `[File path/link]`
  seed data                                                      
  -------------------------------------------------------------------------------------------------------------

For each incomplete item, state current behavior, evidence, and
limitation honestly. Do not label an incomplete footprint as complete.

## Security

-   Do not commit passwords, database credentials, JWT secrets, or API
    keys.
-   Share demo credentials only with the authorized evaluator.
-   Use dedicated test accounts and rotate shared credentials after
    evaluation.
-   Verify that backend authorization matches the intended role and
    organization boundary.

## Related links

-   [Swagger
    UI](https://carbon-backend-xhmi.onrender.com/swagger-ui/index.html)
-   [OpenAPI JSON](https://carbon-backend-xhmi.onrender.com/v3/api-docs)
-   [Frontend
    repository](https://github.com/Ramchandra-18/carbon-frontend)
-   [Backend
    repository](https://github.com/Ramchandra-18/carbon-backend)
-   [Live application](https://carbon-frontend-zr8y.vercel.app/)

------------------------------------------------------------------------

CarbonLedger --- Java Full Stack Developer Intern assignment prototype.
