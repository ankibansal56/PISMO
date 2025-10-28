#!/bin/bash

# Pismo API - Quick Deployment Script for AWS EC2
# Usage: ./deploy.sh

set -e  # Exit on error

echo "🚀 Starting Pismo API Deployment..."

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Configuration
APP_DIR="$HOME/PISMO"
DOCKER_COMPOSE_FILE="docker-compose.yml"

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo -e "${RED}❌ Docker is not installed. Please install Docker first.${NC}"
    exit 1
fi

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}❌ Docker Compose is not installed. Please install Docker Compose first.${NC}"
    exit 1
fi

# Navigate to app directory
if [ ! -d "$APP_DIR" ]; then
    echo -e "${RED}❌ Application directory not found: $APP_DIR${NC}"
    exit 1
fi

cd "$APP_DIR"
echo -e "${GREEN}✅ Changed to directory: $APP_DIR${NC}"

# Stop running containers
echo -e "${YELLOW}🛑 Stopping running containers...${NC}"
docker-compose -f $DOCKER_COMPOSE_FILE down || true

# Remove old images (optional, uncomment to enable)
# echo -e "${YELLOW}🗑️  Removing old images...${NC}"
# docker image prune -f

# Build new images
echo -e "${YELLOW}🔨 Building Docker images...${NC}"
docker-compose -f $DOCKER_COMPOSE_FILE build

# Start containers
echo -e "${YELLOW}🚀 Starting containers...${NC}"
docker-compose -f $DOCKER_COMPOSE_FILE up -d

# Wait for containers to be healthy
echo -e "${YELLOW}⏳ Waiting for containers to be healthy...${NC}"
sleep 10

# Check container status
echo -e "${YELLOW}📊 Container status:${NC}"
docker-compose -f $DOCKER_COMPOSE_FILE ps

# Test health endpoint
echo -e "${YELLOW}🏥 Testing health endpoint...${NC}"
sleep 5
if curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Health check passed!${NC}"
else
    echo -e "${RED}❌ Health check failed!${NC}"
    echo -e "${YELLOW}📋 Application logs:${NC}"
    docker logs pismo-api --tail 50
    exit 1
fi

# Show logs
echo -e "${YELLOW}📋 Recent application logs:${NC}"
docker logs pismo-api --tail 20

# Get public IP
PUBLIC_IP=$(curl -s http://checkip.amazonaws.com)
echo ""
echo -e "${GREEN}╔════════════════════════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║          🎉 Deployment Successful! 🎉                      ║${NC}"
echo -e "${GREEN}╚════════════════════════════════════════════════════════════╝${NC}"
echo ""
echo -e "${GREEN}🌐 Your API is now running at:${NC}"
echo -e "   📍 API Base URL: ${YELLOW}http://$PUBLIC_IP:8080${NC}"
echo -e "   📚 Swagger UI:   ${YELLOW}http://$PUBLIC_IP:8080/swagger-ui.html${NC}"
echo -e "   ❤️  Health Check: ${YELLOW}http://$PUBLIC_IP:8080/actuator/health${NC}"
echo ""
echo -e "${GREEN}📖 Default credentials:${NC}"
echo -e "   👤 Username: ${YELLOW}admin${NC}"
echo -e "   🔑 Password: ${YELLOW}password123${NC}"
echo ""
echo -e "${GREEN}📋 Useful commands:${NC}"
echo -e "   View logs:     ${YELLOW}docker logs pismo-api -f${NC}"
echo -e "   Restart app:   ${YELLOW}docker restart pismo-api${NC}"
echo -e "   Stop all:      ${YELLOW}docker-compose -f $DOCKER_COMPOSE_FILE down${NC}"
echo -e "   Database CLI:  ${YELLOW}docker exec -it pismo-postgres psql -U pismo_user -d pismo_db${NC}"
echo ""
