# Calculator Service

A RESTful calculator built with Spring Boot 3 (Spring Web + Actuator), Maven, and Java 17.

## Build

```bash
mvn clean package
```

Produces the executable JAR at `target/calculator.jar`.

## Test

```bash
mvn test
```

JUnit XML reports (for Jenkins' `junit` step) are written to `target/surefire-reports/TEST-*.xml`.

## Run

```bash
java -jar target/calculator.jar
# or during development
mvn spring-boot:run
```

The service listens on port `8080`.

## API

| Operation | Endpoint                        |
|-----------|---------------------------------|
| Add       | `GET /api/v1/add?a=&b=`         |
| Subtract  | `GET /api/v1/subtract?a=&b=`    |
| Multiply  | `GET /api/v1/multiply?a=&b=`    |
| Divide    | `GET /api/v1/divide?a=&b=`      |
| Health    | `GET /actuator/health`          |

```bash
curl "http://localhost:8080/api/v1/add?a=2&b=3"
# {"operation":"add","a":2,"b":3,"result":5}

curl "http://localhost:8080/api/v1/divide?a=10&b=4"
# {"operation":"divide","a":10,"b":4,"result":2.5}

curl "http://localhost:8080/actuator/health"
# {"status":"UP"}
```

### Errors

Missing parameters, non-numeric values, and division by zero return HTTP 400:

```bash
curl -i "http://localhost:8080/api/v1/divide?a=1&b=0"
```
```json
{
  "timestamp": "2026-10-17T12:00:00.000Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Division by zero is not allowed",
  "path": "/api/v1/divide"
}
```

Division uses 16 significant digits (e.g. `1/3` → `0.3333333333333333`). 
