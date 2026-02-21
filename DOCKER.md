# Docker Deployment Guide

This guide explains how to build and deploy the Self-Investment Transaction API as a Docker container on port 5477.

## Quick Start

### Option 1: Pull & Run from Docker Hub (Easiest)

```bash
docker run -d \
  --name selfinvestment-api \
  -p 5477:5477 \
  -e APP_SECURITY_API_KEY=akhilsharma \
  -e CORS_ALLOWED_ORIGINS=http://localhost:5477 \
  akhil2020171/selfinvestment-api:latest
```

Test:
```bash
curl -H "X-API-KEY: akhilsharma" http://localhost:5477/blackrock/challenge/v1/performance
```

### Option 2: Build Locally

```bash
# Clone repository
git clone https://github.com/Akhil-2020171/BlackRock-Hackathon.git
cd BlackRock-Hackathon/selfinvestment

# Build image
docker build -t akhil2020171/selfinvestment-api:latest .

# Run container
docker run -d \
  --name selfinvestment-api \
  -p 5477:5477 \
  -e APP_SECURITY_API_KEY=akhilsharma \
  -e CORS_ALLOWED_ORIGINS=http://localhost:5477 \
  akhil2020171/selfinvestment-api:latest

# Test
curl -H "X-API-KEY: akhilsharma" http://localhost:5477/blackrock/challenge/v1/performance
```

---

## Container Details

| Property | Value |
|----------|-------|
| **Image Name** | `akhil2020171/selfinvestment-api` |
| **Port** | `5477` |
| **Base Image** | `eclipse-temurin:21-jre-jammy` |
| **Java Version** | JDK 21 |
| **Build Tool** | Maven 3.9 |
| **Application** | Spring Boot 4.0.3 |

---

## Environment Variables

Configure these when running the container:

```bash
SERVER_PORT=5477                              # Application port
SPRING_APPLICATION_NAME=selfinvestment        # App name
APP_SECURITY_API_KEY=akhilsharma             # API key for authentication
CORS_ALLOWED_ORIGINS=http://localhost:5477   # CORS allowed origin
```

---

## Docker Hub Registry

**Public Image URL:**
```
docker.io/akhil2020171/selfinvestment-api:latest
```

**Docker Hub Link:**
```
https://hub.docker.com/r/akhil2020171/selfinvestment-api
```

### Push to Docker Hub (For Repository Owner)

```bash
# Build image
docker build -t akhil2020171/selfinvestment-api:latest .

# Tag with version
docker tag akhil2020171/selfinvestment-api:latest akhil2020171/selfinvestment-api:v1.0.0

# Login to Docker Hub
docker login

# Push images
docker push akhil2020171/selfinvestment-api:latest
docker push akhil2020171/selfinvestment-api:v1.0.0
```

---

## GitHub Container Registry (GHCR)

**Public Image URL:**
```
ghcr.io/akhil-2020171/selfinvestment-api:latest
```

**GHCR Link:**
```
https://github.com/Akhil-2020171/BlackRock-Hackathon/pkgs/container/selfinvestment-api
```

### Push to GHCR (For Repository Owner)

```bash
# Create PAT token with write:packages scope
# Then login:
echo YOUR_GITHUB_TOKEN | docker login ghcr.io -u YOUR_USERNAME --password-stdin

# Build and tag
docker build -t ghcr.io/akhil-2020171/selfinvestment-api:latest .

# Push
docker push ghcr.io/akhil-2020171/selfinvestment-api:latest
```

---

## Docker Compose (Recommended for Development)

**File:** `docker-compose.yml`

```bash
# Start
docker-compose up -d

# View logs
docker-compose logs -f

# Stop
docker-compose down
```

---

## Network & Port Mapping

```bash
# Container → Host Port Mapping
Container Port 5477 → Host Port 5477

# Access Points
- localhost:5477         (local machine)
- 192.168.1.8:5477      (network machines)
- container-ip:5477     (from other containers)
```

---

## Health Check

Docker Compose includes a health check:

```bash
# Check status
docker-compose ps

# Manual health test
curl -H "X-API-KEY: akhilsharma" http://localhost:5477/blackrock/challenge/v1/performance
```

---

## Testing the APIs

Once container is running:

```bash
# Parse Transactions
curl -X POST http://localhost:5477/blackrock/challenge/v1/transactions:parse \
  -H "X-API-KEY: akhilsharma" \
  -H "Content-Type: application/json" \
  -d '[{"date":"2023-03-20 14:45:00","amount":3500,"ceiling":0,"remanent":0}]'

# Validate Transactions
curl -X POST http://localhost:5477/blackrock/challenge/v1/transactions:validator \
  -H "X-API-KEY: akhilsharma" \
  -H "Content-Type: application/json" \
  -d '{"wage":50000,"transactions":[{"date":"2023-03-20 14:45:00","amount":3500,"ceiling":3600,"remanent":100}]}'

# Performance
curl -H "X-API-KEY: akhilsharma" http://localhost:5477/blackrock/challenge/v1/performance
```

---

## Troubleshooting

### Container won't start
```bash
docker logs selfinvestment-api
```

### Port already in use
```bash
docker ps -a
docker stop <container-id>
```

### API not responding
```bash
# Check if container is running
docker ps | grep selfinvestment

# Check port mapping
docker port selfinvestment-api

# Test connectivity
docker inspect selfinvestment-api | grep IPAddress
```

### Rebuild without cache
```bash
docker build --no-cache -t akhil2020171/selfinvestment-api:latest .
```

---

## Image Size

**Multi-stage build optimization:**
- Builder stage: ~800MB (includes Maven & JDK)
- Final image: ~300MB (JRE only)

**Base images:**
- maven:3.9-eclipse-temurin-21 (builder)
- eclipse-temurin:21-jre-jammy (runtime)

---

## CI/CD Automation

GitHub Actions workflow (`/.github/workflows/docker-publish.yml`) automatically:
- Builds on push to `main` or `master`
- Pushes to Docker Hub on tag release (`v*`)
- Pushes to GHCR on tag release
- Includes health check testing

**Secret Configuration Required:**
```
DOCKER_HUB_USERNAME
DOCKER_HUB_TOKEN
```

---

## Production Deployment

For production, consider:

```bash
# Run with resource limits
docker run -d \
  --name selfinvestment-api \
  -p 5477:5477 \
  --memory="512m" \
  --cpus="1.0" \
  -e APP_SECURITY_API_KEY=<secure-key> \
  -e CORS_ALLOWED_ORIGINS=<production-domain> \
  --restart always \
  akhil2020171/selfinvestment-api:latest
```

Or use Kubernetes/Docker Swarm for orchestration.

---

## Support

- **Issues:** GitHub Issues
- **Docker Hub:** `docker.io/akhil2020171/selfinvestment-api`
- **GHCR:** `ghcr.io/akhil-2020171/selfinvestment-api`

