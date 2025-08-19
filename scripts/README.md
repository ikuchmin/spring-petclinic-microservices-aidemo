# Scripts Directory

This directory contains various utility scripts for managing the Spring PetClinic microservices application.

## rebuild-and-run.sh

A comprehensive script for rebuilding all service images and managing the Docker Compose environment.

### Features

- **Maven Integration**: Uses the Maven wrapper (`mvnw`) to build all Spring Boot services
- **Docker Image Building**: Attempts to use Spring Boot's `build-image` goal, falls back to Docker build if needed
- **Docker Compose Management**: Handles starting, stopping, and restarting all services
- **Service-Specific Operations**: Can operate on individual services or all services
- **Flexible Options**: Supports various command-line options for different use cases
- **Colored Output**: Provides clear, colored status messages during execution
- **Error Handling**: Robust error handling with informative messages

### Usage

```bash
# Basic usage - stop and restart all services (default)
./scripts/rebuild-and-run.sh

# Show help
./scripts/rebuild-and-run.sh -h

# List all available services
./scripts/rebuild-and-run.sh -l

# Only build images for all services
./scripts/rebuild-and-run.sh -b

# Build images for specific services
./scripts/rebuild-and-run.sh -b customers-service
./scripts/rebuild-and-run.sh -b api-gateway config-server

# Only stop all services
./scripts/rebuild-and-run.sh -d

# Stop specific services
./scripts/rebuild-and-run.sh -d api-gateway

# Only start all services
./scripts/rebuild-and-run.sh -u

# Start specific services
./scripts/rebuild-and-run.sh -u config-server customers-service

# Clean build and restart all services
./scripts/rebuild-and-run.sh -c

# Rebuild specific services with clean
./scripts/rebuild-and-run.sh -r -c customers-service
```

### Options

| Option | Long Option | Description |
|--------|-------------|-------------|
| `-h` | `--help` | Show help message |
| `-b` | `--build` | Build specified service images using Maven |
| `-d` | `--down` | Stop and remove specified service containers |
| `-u` | `--up` | Start specified services using Docker Compose |
| `-r` | `--rebuild` | Rebuild specified images and restart services |
| `-c` | `--clean` | Clean Maven target directories before building |
| `-l` | `--list` | List all available services |

### Service Names

You can specify one or more service names to operate on. If no services are specified, all services will be processed.

**Available Services:**
- `config-server` - Configuration server
- `customers-service` - Customer management service
- `vets-service` - Veterinarian management service
- `visits-service` - Visit management service
- `genai-service` - AI chatbot service
- `api-gateway` - API gateway service
- `admin-server` - Admin monitoring service

**Service Name Examples:**
```bash
# Use short names (recommended)
./scripts/rebuild-and-run.sh -b customers-service

# Use full names (also supported)
./scripts/rebuild-and-run.sh -b spring-petclinic-customers-service

# Multiple services
./scripts/rebuild-and-run.sh -u config-server customers-service vets-service

# Mix of short and full names
./scripts/rebuild-and-run.sh -d spring-petclinic-api-gateway visits-service
```

### What It Does

1. **Build Process**:
   - Compiles all Spring Boot applications using Maven (for dependencies)
   - Builds Docker images for specified services using Spring Boot's `build-image` goal
   - Falls back to Docker build if `build-image` is not available
   - Supports building individual services or all services

2. **Docker Compose Management**:
   - Can stop/start all services or specific services
   - Stops and removes existing containers when rebuilding
   - Starts services in the correct order
   - Provides status information and helpful commands

3. **Error Handling**:
   - Validates service names before processing
   - Continues building other services if one fails
   - Provides clear error messages
   - Exits gracefully on critical failures

### Prerequisites

- Maven wrapper (`mvnw`) must be present in the project root
- Docker and Docker Compose must be installed and running
- `docker-compose.yml` must be present in the project root
- Sufficient permissions to execute Maven and Docker commands

### Examples

#### Complete Rebuild
```bash
# This will stop all services, rebuild all images, and restart everything
./scripts/rebuild-and-run.sh -r
```

#### Quick Restart
```bash
# This will stop and restart all services without rebuilding (default behavior)
./scripts/rebuild-and-run.sh
```

#### Service-Specific Operations
```bash
# Rebuild only the customers service
./scripts/rebuild-and-run.sh -r customers-service

# Build and start only the API gateway
./scripts/rebuild-and-run.sh -b -u api-gateway

# Stop only the visits service
./scripts/rebuild-and-run.sh -d visits-service

# Start only the config server and customers service
./scripts/rebuild-and-run.sh -u config-server customers-service
```

#### Development Workflow
```bash
# After making changes to customers service, rebuild just that service
./scripts/rebuild-and-run.sh -r customers-service

# If you want a completely clean build for specific services
./scripts/rebuild-and-run.sh -r -c api-gateway vets-service

# Quick restart during development (no rebuild)
./scripts/rebuild-and-run.sh
```

#### Troubleshooting
```bash
# List all available services
./scripts/rebuild-and-run.sh -l

# Stop specific services to investigate issues
./scripts/rebuild-and-run.sh -d api-gateway

# Start services after fixing issues
./scripts/rebuild-and-run.sh -u api-gateway
```

### Troubleshooting

- **Permission Denied**: Make sure the script is executable (`chmod +x scripts/rebuild-and-run.sh`)
- **Maven Wrapper Missing**: Ensure `mvnw` exists in the project root
- **Docker Not Running**: Start Docker service before running the script
- **Port Conflicts**: The script will stop existing services, but check for other processes using the same ports
- **Unknown Service**: Use `-l` flag to see all available service names
- **Service Validation**: The script validates service names before processing

### Notes

- The script attempts to use Spring Boot's `build-image` goal first, which is the recommended approach for Spring Boot applications
- If `build-image` fails, it falls back to traditional Docker builds using Dockerfiles
- All services are built with the version tag `3.4.1` to match the docker-compose.yml configuration
- The script provides colored output for better readability and user experience
- Service names can be specified using either short names (e.g., `customers-service`) or full names (e.g., `spring-petclinic-customers-service`)
- When building specific services, the script still compiles all modules first to ensure dependencies are available
