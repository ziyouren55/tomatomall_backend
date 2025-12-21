# Start Kafka (docker-compose) and then start backend via Maven
Write-Host "Starting Zookeeper and Kafka via docker-compose..."
docker-compose up -d

Write-Host "Waiting 6 seconds for Kafka to initialize..."
Start-Sleep -Seconds 6

Write-Host "Starting backend (TomatoMall_backend) via Maven..."
Push-Location .\TomatoMall_backend
mvn -DskipTests spring-boot:run
Pop-Location


