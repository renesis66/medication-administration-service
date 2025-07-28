#!/bin/bash

echo "🚀 Setting up local development environment..."

# Start DynamoDB-local using Docker Compose
echo "📦 Starting DynamoDB-local..."
docker-compose up -d dynamodb-local

# Wait for DynamoDB to be ready
echo "⏳ Waiting for DynamoDB-local to be ready..."
sleep 5

# Check if DynamoDB is responding
until curl -s http://localhost:8000 > /dev/null 2>&1; do
    echo "⏳ Still waiting for DynamoDB-local..."
    sleep 2
done

echo "✅ DynamoDB-local is ready!"

# Create the table
echo "🗃️ Creating administrations table..."
./scripts/create-dynamodb-table.sh

echo "🎉 Local development environment is ready!"
echo ""
echo "🔧 Next steps:"
echo "  1. Run: ./gradlew run"
echo "  2. Your API will be available at: http://localhost:8080/medication"
echo "  3. DynamoDB Admin UI (if installed): http://localhost:8000/shell"
echo ""
echo "📊 To stop DynamoDB-local: docker-compose down"