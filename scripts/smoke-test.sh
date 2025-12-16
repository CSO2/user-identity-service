#!/bin/bash

# Configuration
SERVICE_URL=${1:-"http://localhost:8080"}
MAX_RETRIES=30
SLEEP_INTERVAL=5

echo "Starting smoke tests against $SERVICE_URL..."

# Function to check health
check_health() {
    local url="$1/actuator/health"
    
    # Get HTTP response code and body
    local response=$(curl -s -w "\n%{http_code}" "$url")
    local body=$(echo "$response" | sed '$d')
    local http_code=$(echo "$response" | tail -n1)
    
    if [ "$http_code" == "200" ]; then
        # Check if status is UP
        if echo "$body" | grep -q '"status":"UP"'; then
            return 0
        else
            echo "Health check returned 200 but status is not UP: $body"
            return 1
        fi
    else
        echo "Health check returned HTTP $http_code"
        return 1
    fi
}

# Wait for service to be ready
echo "Waiting for service to be up..."
for ((i=1; i<=MAX_RETRIES; i++)); do
    if check_health "$SERVICE_URL"; then
        echo "✅ Service is UP and healthy!"
        
        # Print health details
        echo "Health endpoint response:"
        curl -s "$SERVICE_URL/actuator/health" | head -c 500
        echo ""
        
        exit 0
    fi
    echo "Attempt $i/$MAX_RETRIES: Service not ready yet... waiting ${SLEEP_INTERVAL}s"
    sleep $SLEEP_INTERVAL
done

echo "❌ Service failed to start within timeout."
echo "Final health check attempt:"
curl -s "$SERVICE_URL/actuator/health" || echo "Could not reach service"
exit 1
