# Self-Investment Transaction API

A secure Spring Boot REST API for managing retirement self-investment transactions with comprehensive validation, filtering, and performance monitoring capabilities.

## Project Overview

The Self-Investment Transaction API provides endpoints for parsing, validating, filtering, and monitoring investment transactions. It implements Spring Security with API-key authentication and CORS support for secure cross-origin requests.

**Project Context**: BlackRock Hackathon Challenge - Transaction Builder API

## Features

✅ **Transaction Management**
- Parse transaction data with automatic ceiling/remainder calculations
- Validate transactions against wage limits
- Filter transactions using complex business rules (Q, P, K periods)
- Detect duplicate transactions

✅ **Security**
- API-key based authentication (X-API-KEY header)
- Server-side Origin validation with configurable CORS
- Stateless session management (STATELESS)
- CSRF protection disabled (REST API)

✅ **Performance Monitoring**
- Real-time performance metrics (response time, memory usage, thread count)
- Detailed duration formatting (HH:MM:SS.mmm)

✅ **Data Integrity**
- Input validation for transaction amounts
- Wage limit enforcement
- Duplicate detection
- Proper date format handling (yyyy-MM-dd HH:mm:ss)

## Architecture

```
selfinvestment/
├── controller/
│   ├── SelfInvestmentController.java      # Transaction endpoints
│   └── PerformaceController.java          # Performance endpoint
├── service/
│   ├── TransactionService.java            # Business logic
│   └── PerformanceService.java            # Metrics collection
├── models/                                # DTOs
├── authorization/
│   ├── ApiKeyFilter.java                 # API-key authentication
│   ├── OriginFilter.java                 # CORS & Origin validation
│   └── SecurityConfig.java               # Spring Security configuration
└── resources/
    └── application.properties             # Configuration
```

## API Endpoints

### 1. Parse Transactions
Parses a list of transactions and calculates ceiling amount and remainder for each transaction.

**Endpoint:** `POST /blackrock/challenge/v1/transactions:parse`

**Authentication:** Required (X-API-KEY header)

**Request:**
```json
[
  {
    "date": "2023-03-20 14:45:00",
    "amount": 3500,
    "ceiling": 0,
    "remanent": 0
  },
  {
    "date": "2023-06-10 09:15:00",
    "amount": 1500,
    "ceiling": 0,
    "remanent": 0
  }
]
```

**Response:**
```json
[
  {
    "date": "2023-03-20 14:45:00",
    "amount": 3500,
    "ceiling": 3600,
    "remanent": 100
  },
  {
    "date": "2023-06-10 09:15:00",
    "amount": 1500,
    "ceiling": 1600,
    "remanent": 100
  }
]
```

**Calculation:**
- `ceiling = Math.ceil(amount / 100) * 100` (rounds up to nearest 100)
- `remanent = ceiling - amount` (remainder needed to reach ceiling)

---

### 2. Validate Transactions
Validates transactions against a wage limit and identifies invalid/duplicate transactions.

**Endpoint:** `POST /blackrock/challenge/v1/transactions:validator`

**Authentication:** Required (X-API-KEY header)

**Request:**
```json
{
  "wage": 50000,
  "transactions": [
    {
      "date": "2023-03-20 14:45:00",
      "amount": 3500,
      "ceiling": 3600,
      "remanent": 100
    },
    {
      "date": "2023-07-10 09:15:00",
      "amount": -250,
      "ceiling": 0,
      "remanent": 0
    }
  ]
}
```

**Response:**
```json
{
  "valid": [
    {
      "date": "2023-03-20 14:45:00",
      "amount": 3500,
      "ceiling": 3600,
      "remanent": 100
    }
  ],
  "invalid": [
    {
      "date": "2023-07-10 09:15:00",
      "amount": -250,
      "ceiling": 0,
      "remanent": 0,
      "message": "Negative or zero amount is not allowed"
    }
  ]
}
```

**Validation Rules:**
- ✓ Amount must be > 0
- ✓ Amount must not exceed wage
- ✓ No duplicate transactions (same date + amount)

---

### 3. Filter & Validate Transactions
Filters transactions using complex business rules (Q, P, K periods) and applies additional validations.

**Endpoint:** `POST /blackrock/challenge/v1/transactions:filter`

**Authentication:** Required (X-API-KEY header)

**Request:**
```json
{
  "wage": 50000,
  "transactions": [
    {
      "date": "2023-03-20 14:45:00",
      "amount": 3500,
      "ceiling": 0,
      "remanent": 0
    }
  ],
  "q": [
    {
      "start": "2023-01-01 00:00:00",
      "end": "2023-12-31 23:59:59",
      "fixed": 500
    }
  ],
  "p": [
    {
      "start": "2023-06-01 00:00:00",
      "end": "2023-08-31 23:59:59",
      "extra": 250
    }
  ],
  "k": [
    {
      "start": "2023-07-01 00:00:00",
      "end": "2023-07-31 23:59:59"
    }
  ]
}
```

**Response:**
```json
{
  "valid": [
    {
      "date": "2023-03-20 14:45:00",
      "amount": 3500,
      "ceiling": 4000,
      "remanent": 500,
      "inKPeriod": false
    }
  ],
  "invalid": []
}
```

**Business Rules:**
- **Q Rule**: Fixed remainder for date ranges (overrides calculated remainder)
- **P Rule**: Extra amount added to remainder for date ranges
- **K Rule**: Marks transactions in special K-period ranges
- Same validation as validator endpoint applies after filtering

---

### 4. Performance Metrics
Returns real-time performance metrics including response time, memory usage, and thread count.

**Endpoint:** `GET /blackrock/challenge/v1/performance`

**Authentication:** Required (X-API-KEY header)

**Request:**
```bash
curl -H "X-API-KEY: akhilsharma" http://192.168.1.8:8080/blackrock/challenge/v1/performance
```

**Response:**
```json
{
  "durationTime": "00:00:00.145",
  "memoryUsage": "125.32",
  "threadCount": 24
}
```

**Metrics:**
- `durationTime`: Request processing time (HH:MM:SS.mmm format)
- `memoryUsage`: Heap memory in use (MB)
- `threadCount`: Active thread count

---

## Security Configuration

### Authentication
All endpoints are protected by API-key authentication via the `X-API-KEY` header.

**Configuration File:** `src/main/resources/application.properties`
```properties
app.security.api-key=akhilsharma
```

**How to Use:**
```bash
curl -H "X-API-KEY: akhilsharma" \
     -H "Content-Type: application/json" \
     -H "Origin: http://localhost:8080" \
     -X POST http://localhost:8080/blackrock/challenge/v1/transactions:parse
```

### CORS
Server-side Origin validation is enforced. Only requests from configured origins are accepted.

**Configuration:**
```properties
cors.allowed.origins=http://localhost:8080
```

**Allowed Methods:** GET, POST, PUT, DELETE, OPTIONS

**Allowed Headers:** Authorization, Content-Type, Accept, X-API-KEY

### Features
- ✅ Stateless session management
- ✅ CSRF protection
- ✅ Server-side Origin rejection (403 if origin not allowed)
- ✅ Inline Origin validation before API-key check

---

## Setup & Installation

### Prerequisites
- **Java 21** or higher
- **Maven 3.8+**
- **Spring Boot 4.0.3**

### Build
```bash
cd selfinvestment
./mvnw.cmd clean package -DskipTests
```

### Run
```bash
# Using Maven
./mvnw.cmd spring-boot:run

# Or run the JAR directly
java -jar target/selfinvestment-0.0.1.jar
```

### Access
- **Base URL:** `http://localhost:8080` or `http://192.168.1.8:8080`
- **API Base:** `/blackrock/challenge/v1`

---

## Docker Setup & Deployment

### Build Docker Image Locally

#### Build and Tag
```bash
# Build the Docker image
docker build -t akhil2020171/selfinvestment-api:latest .

# Or with a specific version
docker build -t akhil2020171/selfinvestment-api:v1.0.0 .
```

#### Test Locally
```bash
# Run the container
docker run -d \
  --name selfinvestment-api \
  -p 5477:5477 \
  -e APP_SECURITY_API_KEY=akhilsharma \
  -e CORS_ALLOWED_ORIGINS=http://localhost:5477 \
  akhil2020171/selfinvestment-api:latest

# Test the API
curl -H "X-API-KEY: akhilsharma" \
     http://localhost:5477/blackrock/challenge/v1/performance

# View logs
docker logs selfinvestment-api

# Stop the container
docker stop selfinvestment-api
docker rm selfinvestment-api
```

---

### Push to Docker Hub

#### Prerequisites
1. Create a [Docker Hub](https://hub.docker.com) account
2. Login locally:
```bash
docker login
# Enter your Docker Hub username and password
```

#### Push Commands
```bash
# Build and tag image
docker build -t akhil2020171/selfinvestment-api:latest .
docker build -t akhil2020171/selfinvestment-api:v1.0.0 .

# Push to Docker Hub
docker push akhil2020171/selfinvestment-api:latest
docker push akhil2020171/selfinvestment-api:v1.0.0

# Verify push
docker pull akhil2020171/selfinvestment-api:latest
```

#### Docker Hub Image URL
```
docker.io/akhil2020171/selfinvestment-api:latest
docker.io/akhil2020171/selfinvestment-api:v1.0.0
```

---

### Push to GitHub Container Registry (GHCR)

#### Prerequisites
1. Create a [GitHub Personal Access Token (PAT)](https://github.com/settings/tokens) with `write:packages` scope
2. Login to GHCR:
```bash
echo $GITHUB_TOKEN | docker login ghcr.io -u USERNAME --password-stdin
```

#### Push Commands
```bash
# Build and tag image for GHCR
docker build -t ghcr.io/akhil-2020171/selfinvestment-api:latest .
docker build -t ghcr.io/akhil-2020171/selfinvestment-api:v1.0.0 .

# Push to GHCR
docker push ghcr.io/akhil-2020171/selfinvestment-api:latest
docker push ghcr.io/akhil-2020171/selfinvestment-api:v1.0.0

# Verify push (pull)
docker pull ghcr.io/akhil-2020171/selfinvestment-api:latest
```

#### GHCR Image URL
```
ghcr.io/akhil-2020171/selfinvestment-api:latest
ghcr.io/akhil-2020171/selfinvestment-api:v1.0.0
```

---

### Run from Docker Hub / GHCR

#### From Docker Hub
```bash
docker run -d \
  --name selfinvestment-api \
  -p 5477:5477 \
  -e APP_SECURITY_API_KEY=akhilsharma \
  -e CORS_ALLOWED_ORIGINS=http://localhost:5477 \
  docker.io/akhil2020171/selfinvestment-api:latest
```

#### From GHCR
```bash
docker run -d \
  --name selfinvestment-api \
  -p 5477:5477 \
  -e APP_SECURITY_API_KEY=akhilsharma \
  -e CORS_ALLOWED_ORIGINS=http://localhost:5477 \
  ghcr.io/akhil-2020171/selfinvestment-api:latest
```

---

### Docker Compose

#### Run with Docker Compose
```bash
docker-compose up -d
```

#### Docker Compose File Content
**File:** `docker-compose.yml`
```yaml
version: '3.8'

services:
  selfinvestment-api:
    image: akhil2020171/selfinvestment-api:latest
    container_name: selfinvestment-api
    ports:
      - "5477:5477"
    environment:
      SERVER_PORT: 5477
      SPRING_APPLICATION_NAME: selfinvestment
      APP_SECURITY_API_KEY: akhilsharma
      CORS_ALLOWED_ORIGINS: http://localhost:5477
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:5477/blackrock/challenge/v1/performance"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
```

#### Manage Compose
```bash
# Start services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

---

### Dockerfile Details

**File:** `Dockerfile`

**Two-stage build:**
1. **Builder Stage:** Compiles the application using Maven on JDK 21
2. **Runtime Stage:** Runs the application on a lightweight JRE

**Key Features:**
- Multi-stage build for minimal image size
- Exposes port 5477
- Environment variable `SERVER_PORT=5477`
- Optimized for production deployment

---

### Docker Best Practices Applied

✅ **Multi-stage Build** — Reduces final image size (excludes Maven and build artifacts)  
✅ **Minimal Base Image** — eclipse-temurin:21-jre-jammy (JRE only, no build tools)  
✅ **Explicit Port** — 5477 clearly documented and exposed  
✅ **Environment Configuration** — Port and application settings via environment variables  
✅ **.dockerignore** — Excludes unnecessaryfiles from build context  
✅ **Health Checks** — Docker Compose includes health check endpoint  
✅ **Restart Policy** — Configured to auto-restart on failure  

---

### Troubleshooting

**Port already in use:**
```bash
# Find and kill process on port 5477
docker ps -a | grep 5477
docker stop <container-id>
```

**Image not found:**
```bash
# Rebuild the image
docker build -t akhil2020171/selfinvestment-api:latest .

# Or pull from registry
docker pull docker.io/akhil2020171/selfinvestment-api:latest
```

**Container won't start:**
```bash
# Check logs
docker logs selfinvestment-api

# Inspect container
docker inspect selfinvestment-api

# Run with interactive terminal for debugging
docker run -it --rm akhil2020171/selfinvestment-api:latest
```

**API not responding:**
```bash
# Check health
curl -H "X-API-KEY: akhilsharma" http://localhost:5477/blackrock/challenge/v1/performance

# Verify port mapping
docker port selfinvestment-api

# Check container IP
docker inspect selfinvestment-api | grep IPAddress
```

---

## Testing with Postman

### Set up Headers
For all requests, include:
```
X-API-KEY: akhilsharma
Content-Type: application/json
Origin: http://localhost:8080
```

### Example: Test Parse Endpoint
1. Create a new POST request
2. URL: `http://192.168.1.8:8080/blackrock/challenge/v1/transactions:parse`
3. Headers: (as above)
4. Body (raw JSON):
```json
[
  {
    "date": "2023-03-20 14:45:00",
    "amount": 3500,
    "ceiling": 0,
    "remanent": 0
  }
]
```
5. Click Send

---

## Configuration Properties

**File:** `src/main/resources/application.properties`

```properties
# Application
spring.application.name=selfinvestment
server.port=8080

# Security
app.security.api-key=akhilsharma
cors.allowed.origins=http://localhost:8080
```

### Update Configuration
- Change API key before production deployment
- Add multiple origins: `cors.allowed.origins=http://localhost:8080,http://192.168.1.8:8080`
- Adjust server port as needed

---

## Error Handling

### Invalid API Key
**Response:** `401 Unauthorized`
```
Missing or invalid API key
```

### Origin Not Allowed
**Response:** `403 Forbidden`
```
Origin not allowed
```

### Invalid Request Body
**Response:** `400 Bad Request`
```
JSON parse error or validation error
```

---

## Data Models

### transactionsDTO
```java
class transactionsDTO {
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    Date date;
    double amount;
    double ceiling;
    double remanent;
}
```

### transactionValidatorDTO
```java
class transactionValidatorDTO {
    List<transactionResponseDTO> transactions;
    double wage;
}
```

### transactionFilterDTO
```java
class transactionFilterDTO {
    List<transactionsDTO> transactions;
    double wage;
    List<qMomentsDTO> q;  // Q rules
    List<pMomentsDTO> p;  // P rules
    List<kGroupsDTO> k;   // K rules
}
```

### PerformanceResponseDTO
```java
class PerformanceResponseDTO {
    String durationTime;  // HH:MM:SS.mmm
    String memoryUsage;   // MB (2 decimals)
    int threadCount;      // Active threads
}
```

---

## Technologies & Dependencies

- **Spring Boot:** 4.0.3
- **Spring Security:** 7.0.3
- **Spring Web MVC:** Web Starter
- **Java:** 21 (JDK)
- **Lombok:** Annotation processing
- **Jackson:** JSON serialization with date format support
- **Maven:** Build & dependency management

---

## Development Notes

### Date Format
All date fields use format: `yyyy-MM-dd HH:mm:ss`

Serialization/Deserialization configured via `@JsonFormat`:
```java
@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
Date date;
```

### Calculation Logic
**Ceiling Calculation:**
- Rounds up to the nearest 100
- Formula: `Math.ceil(amount / 100) * 100`

**Remainder:**
- Difference between ceiling and original amount
- Formula: `ceiling - amount`

### Transaction Validation Order
1. Check amount > 0
2. Check amount ≤ wage
3. Check for duplicates (same date + amount)

---

## Future Enhancements

- [ ] Database integration (JPA/Hibernate)
- [ ] Comprehensive unit tests
- [ ] API documentation (Swagger/OpenAPI)
- [ ] Rate limiting
- [ ] Audit logging
- [ ] Batch transaction processing
- [ ] Export to CSV/PDF
- [ ] Dashboard UI

---

## Support & Issues

For issues or questions:
1. Check the API response status code and message
2. Verify API key and CORS origin are correctly configured
3. Ensure date format matches `yyyy-MM-dd HH:mm:ss`
4. Check logs in the application console

---

## License

BlackRock Hackathon Challenge

---

**Last Updated:** February 21, 2026  
**Version:** 0.0.1
