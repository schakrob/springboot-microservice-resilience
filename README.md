# Spring Boot Microservice with Resilience4j

This project demonstrates a Spring Boot microservice that calls another microservice with comprehensive resilience patterns using Resilience4j.

## Features

- **Spring Boot 3.3.0** - Latest Spring Boot version
- **Resilience4j 2.1.0** - Resilience patterns implementation
- **Two REST Client Implementations**:
  1. **Supplier Pattern** - Programmatic decorator application with chaining
  2. **Annotation Pattern** - Declarative annotations for resilience

## Resilience Patterns Applied

### 1. Circuit Breaker
- Prevents cascading failures
- States: CLOSED, OPEN, HALF_OPEN
- Configuration:
  - Failure rate threshold: 50%
  - Wait duration in open state: 5 seconds
  - Sliding window size: 10

### 2. Retry
- Retries failed operations
- Maximum attempts: 3
- Wait duration between retries: 1 second
- Retries on: IOException, TimeoutException

### 3. Rate Limiter
- Limits the number of concurrent requests
- Limit: 10 requests per minute
- Prevents service overload

### 4. Bulkhead
- Limits concurrent calls
- Maximum concurrent calls: 5
- Maximum wait duration: 20ms
- Isolates resources

## Project Structure

```
src/main/java/com/example/
├── MicroserviceApplication.java
├── config/
│   └── RestTemplateConfig.java
├── client/
│   ├── UserServiceClientWithSupplier.java
│   └── UserServiceClientWithAnnotations.java
├── controller/
│   └── UserController.java
└── dto/
    ├── UserDto.java
    └── ApiResponse.java

src/main/resources/
├── application.yml
└── logback-spring.xml (optional)
```

## Build and Run

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Build
```bash
mvn clean package
```

### Run
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Supplier Pattern Endpoints

#### Get User by ID
```bash
GET http://localhost:8080/api/proxy/supplier/users/{id}
```

#### Get All Users
```bash
GET http://localhost:8080/api/proxy/supplier/users
```

#### Create User
```bash
POST http://localhost:8080/api/proxy/supplier/users
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "+1234567890"
}
```

### Annotation Pattern Endpoints

#### Get User by ID
```bash
GET http://localhost:8080/api/proxy/annotation/users/{id}
```

#### Get All Users
```bash
GET http://localhost:8080/api/proxy/annotation/users
```

#### Create User
```bash
POST http://localhost:8080/api/proxy/annotation/users
Content-Type: application/json

{
  "name": "Jane Doe",
  "email": "jane@example.com",
  "phone": "+0987654321"
}
```

#### Update User
```bash
PUT http://localhost:8080/api/proxy/annotation/users/{id}
Content-Type: application/json

{
  "name": "Updated Name",
  "email": "updated@example.com",
  "phone": "+1111111111"
}
```

#### Delete User
```bash
DELETE http://localhost:8080/api/proxy/annotation/users/{id}
```

### Health Check
```bash
GET http://localhost:8080/api/proxy/health
```

## Actuator Endpoints

View metrics and configurations:

```bash
# View all available endpoints
GET http://localhost:8080/actuator

# View CircuitBreaker status
GET http://localhost:8080/actuator/circuitbreakers

# View Retry configuration
GET http://localhost:8080/actuator/retries

# View RateLimiter status
GET http://localhost:8080/actuator/ratelimiters

# View Bulkhead status
GET http://localhost:8080/actuator/bulkheads

# View metrics
GET http://localhost:8080/actuator/metrics
```

## Configuration

Edit `src/main/resources/application.yml` to customize resilience settings:

```yaml
resilience4j:
  circuitbreaker:
    instances:
      userServiceCB:
        slidingWindowSize: 10
        failureRateThreshold: 50
        waitDurationInOpenState: 5000
        
  retry:
    instances:
      userServiceRetry:
        maxAttempts: 3
        waitDuration: 1000
        
  ratelimiter:
    instances:
      userServiceRateLimit:
        limitRefreshPeriod: 1m
        limitForPeriod: 10
        
  bulkhead:
    instances:
      userServiceBulkhead:
        maxConcurrentCalls: 5
        maxWaitDuration: 20ms
```

## Dependencies

- Spring Boot 3.3.0
- Resilience4j 2.1.0
- Spring AOP
- Lombok
- Jackson

## Testing

To test the resilience patterns:

1. **Circuit Breaker Test**: Make requests until the circuit breaker opens
2. **Retry Test**: Simulate intermittent failures to observe retries
3. **Rate Limiter Test**: Send multiple rapid requests
4. **Bulkhead Test**: Send concurrent requests

## References

- [Resilience4j Documentation](https://resilience4j.readme.io/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Cloud Resilience4j](https://spring.io/projects/spring-cloud-circuitbreaker)

## License

This project is licensed under the MIT License.