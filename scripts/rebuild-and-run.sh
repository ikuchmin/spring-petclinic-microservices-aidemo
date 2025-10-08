#!/usr/bin/env bash

set -o errexit
set -o errtrace
set -o nounset
set -o pipefail

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# List of all available services
ALL_SERVICES=(
    "spring-petclinic-config-server"
    "spring-petclinic-customers-service"
    "spring-petclinic-vets-service"
    "spring-petclinic-visits-service"
    "spring-petclinic-genai-service"
    "spring-petclinic-api-gateway"
)

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to show usage
show_usage() {
    echo "Usage: $0 [OPTIONS] [SERVICE_NAMES...]"
    echo ""
    echo "Options:"
    echo "  -h, --help          Show this help message"
    echo "  -b, --build         Build specified service images using Maven"
    echo "  -d, --down          Stop and remove specified service containers"
    echo "  -u, --up            Start specified services using Docker Compose"
    echo "  -r, --rebuild       Rebuild specified images and restart services"
    echo "  -c, --clean         Clean Maven target directories before building"
    echo "  -l, --list          List all available services"
    echo ""
    echo "Service Names:"
    echo "  Specify one or more service names to operate on. If none specified,"
    echo "  all services will be processed. Available services:"
    for service in "${ALL_SERVICES[@]}"; do
        echo "    - ${service#spring-petclinic-}"
    done
    echo ""
    echo "Examples:"
    echo "  $0                                    # Stop and restart all services (default)"
    echo "  $0 -b                                # Build all service images"
    echo "  $0 -b customers-service               # Build only customers-service image"
    echo "  $0 -d api-gateway                    # Stop only api-gateway service"
    echo "  $0 -u config-server customers-service # Start only config-server and customers-service"
    echo "  $0 -r                                # Rebuild and restart all services"
    echo "  $0 -r -c                             # Clean build and restart all services"
    echo "  $0 -l                                # List all available services"
}

# Function to list all available services
list_services() {
    echo "Available services:"
    for service in "${ALL_SERVICES[@]}"; do
        echo "  - ${service#spring-petclinic-}"
    done
}

# Function to validate service names
validate_services() {
    local services=("$@")
    local valid_services=()
    
    for service in "${services[@]}"; do
        local found=false
        for valid_service in "${ALL_SERVICES[@]}"; do
            if [ "$service" = "${valid_service#spring-petclinic-}" ] || [ "$service" = "$valid_service" ]; then
                valid_services+=("$valid_service")
                found=true
                break
            fi
        done
        
        if [ "$found" = false ]; then
            print_error "Unknown service: $service"
            print_status "Use -l or --list to see available services"
            return 1
        fi
    done
    
    # Use a global variable to return the array
    VALIDATED_SERVICES=("${valid_services[@]}")
    return 0
}

# Function to build specified service images
build_images() {
    local services=("$@")
    
    print_status "Building specified service images using Maven..."
    
    # Check if Maven wrapper exists
    if [ ! -f "./mvnw" ]; then
        print_error "Maven wrapper (mvnw) not found in current directory"
        exit 1
    fi
    
    # Make Maven wrapper executable
    chmod +x ./mvnw
    
    # Build all modules first (for dependencies)
    print_status "Building Spring Boot applications..."
    ./mvnw clean compile -DskipTests
    
    if [ $? -eq 0 ]; then
        print_success "Maven build completed successfully"
    else
        print_error "Maven build failed"
        exit 1
    fi
    
    # Build Docker images for specified services
    print_status "Building Docker images for specified services..."
    
    for service in "${services[@]}"; do
        if [ -d "$service" ]; then
            print_status "Building image for $service..."
            cd "$service"
            
            # Build the Spring Boot application
            ../mvnw clean package -DskipTests
            
            # Try to build Docker image using Spring Boot Maven plugin build-image goal
            if ../mvnw spring-boot:build-image -DskipTests 2>/dev/null; then
                print_success "Successfully built image for $service using build-image goal"
            else
                print_warning "build-image goal failed for $service, trying alternative Docker build..."
                
                # Alternative: Use Docker build directly if build-image fails
                if [ -f "Dockerfile" ]; then
                    docker build -t "spring-petclinic-${service#spring-petclinic-}:3.4.1" .
                    if [ $? -eq 0 ]; then
                        print_success "Successfully built image for $service using Docker build"
                    else
                        print_error "Failed to build image for $service using Docker build"
                    fi
                else
                    print_warning "No Dockerfile found for $service, skipping Docker image build"
                fi
            fi
            
            cd ..
        else
            print_warning "Service directory $service not found, skipping..."
        fi
    done
    
    print_success "All specified service images built successfully"
}

# Function to stop specified services
stop_services() {
    local services=("$@")
    
    print_status "Stopping specified Docker Compose services..."
    
    if [ ! -f "docker-compose.yml" ]; then
        print_error "docker-compose.yml not found in current directory"
        exit 1
    fi
    
    if [ ${#services[@]} -eq 0 ]; then
        # Stop all services
        docker compose down --remove-orphans
        print_success "All services stopped and removed"
    else
        # Stop specific services
        for service in "${services[@]}"; do
            local service_name="${service#spring-petclinic-}"
            print_status "Stopping $service_name..."
            docker compose stop "$service_name" 2>/dev/null || print_warning "Service $service_name not running or not found"
        done
        print_success "Specified services stopped"
    fi
}

# Function to start specified services
start_services() {
    local services=("$@")
    
    print_status "Starting specified services using Docker Compose..."
    
    if [ ! -f "docker-compose.yml" ]; then
        print_error "docker-compose.yml not found in current directory"
        exit 1
    fi
    
    if [ ${#services[@]} -eq 0 ]; then
        # Start all services
        docker compose up -d
        
        if [ $? -eq 0 ]; then
            print_success "All services started successfully"
            print_status "Services are starting up. You can check status with: docker compose ps"
            print_status "View logs with: docker compose logs -f"
        else
            print_error "Failed to start services"
            exit 1
        fi
    else
        # Start specific services
        for service in "${services[@]}"; do
            local service_name="${service#spring-petclinic-}"
            print_status "Starting $service_name..."
            docker compose up -d "$service_name"
            
            if [ $? -eq 0 ]; then
                print_success "Successfully started $service_name"
            else
                print_warning "Failed to start $service_name"
            fi
        done
        
        print_success "Specified services started"
        print_status "Services are starting up. You can check status with: docker compose ps"
        print_status "View logs with: docker compose logs -f"
    fi
}

# Function to clean Maven target directories
clean_maven() {
    print_status "Cleaning Maven target directories..."
    ./mvnw clean
    print_success "Maven clean completed"
}

# Main script logic
main() {
    local build_images_flag=false
    local stop_services_flag=false
    local start_services_flag=false
    local clean_maven_flag=false
    local list_services_flag=false
    local services=()
    
    # Parse command line arguments
    while [[ $# -gt 0 ]]; do
        case $1 in
            -h|--help)
                show_usage
                exit 0
                ;;
            -b|--build)
                build_images_flag=true
                shift
                ;;
            -d|--down)
                stop_services_flag=true
                shift
                ;;
            -u|--up)
                start_services_flag=true
                shift
                ;;
            -r|--rebuild)
                build_images_flag=true
                stop_services_flag=true
                start_services_flag=true
                shift
                ;;
            -c|--clean)
                clean_maven_flag=true
                shift
                ;;
            -l|--list)
                list_services_flag=true
                shift
                ;;
            -*)
                print_error "Unknown option: $1"
                show_usage
                exit 1
                ;;
            *)
                # Assume it's a service name
                services+=("$1")
                shift
                ;;
        esac
    done
    
    # Handle list services flag
    if [ "$list_services_flag" = true ]; then
        list_services
        exit 0
    fi
    
    # If no flags specified, default to rerun (stop and start, no rebuild)
    if [ "$build_images_flag" = false ] && [ "$stop_services_flag" = false ] && [ "$start_services_flag" = false ]; then
        stop_services_flag=true
        start_services_flag=true
    fi
    
    # Validate service names if specified
    if [ ${#services[@]} -gt 0 ]; then
        print_status "Validating service names..."
        if validate_services "${services[@]}"; then
            services=("${VALIDATED_SERVICES[@]}")
            print_status "Operating on services: ${services[*]#spring-petclinic-}"
        else
            print_error "Service validation failed"
            exit 1
        fi
    else
        print_status "No services specified, operating on all services"
        services=("${ALL_SERVICES[@]}")
    fi
    
    # Execute requested actions
    if [ "$clean_maven_flag" = true ]; then
        clean_maven
    fi
    
    if [ "$stop_services_flag" = true ]; then
        stop_services "${services[@]}"
    fi
    
    if [ "$build_images_flag" = true ]; then
        build_images "${services[@]}"
    fi
    
    if [ "$start_services_flag" = true ]; then
        start_services "${services[@]}"
    fi
    
    print_success "Script execution completed successfully!"
}

# Run main function with all arguments
main "$@"
