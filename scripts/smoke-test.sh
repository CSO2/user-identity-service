#!/bin/bash

# Configuration
SERVICE_URL=${1:-"http://localhost:8080"}
MAX_RETRIES=30
SLEEP_INTERVAL=5

echo "Starting smoke tests against $SERVICE_URL..."

# Function to check health
check_health() {
    local url="$1/actuator/health"
    local response=$(curl -s -o /dev/null -w "%{http_code}" "$url")
    
    if [ "$response" == "200" ]; then
        return 0
    else
        return 1
    fi
}

# Wait for service to be ready
echo "Waiting for service to be up..."
for ((i=1; i<=MAX_RETRIES; i++)); do
    if check_health "$SERVICE_URL"; then
        echo "✅ Service is UP!"
        
        # Verify specific status in JSON response if needed (optional enhancement)
        # curl -s "$SERVICE_URL/actuator/health" | grep "UP"
        
        exit 0
    fi
    echo "Attempt $i/$MAX_RETRIES: Service not ready yet... waiting ${SLEEP_INTERVAL}s"
    sleep $SLEEP_INTERVAL
done

echo "❌ Service failed to start within timeout."
exit 1
