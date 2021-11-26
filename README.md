#  messaging microservice
This project aims to build a backend for messaging application.

## What's inside?
- Kotlin + Spring boot
- Spring web for rest apis
- Spring JPA + Postgres
- Spring HATEOS based response
- ETag configurations for limiting the client on unwanted requests
- ControllerAdvice for exception handling
- RabbitMQ based message pushing
- Junit test cases for controller layer and repository layer
- Fully docker based implementation.
## What all thing I need to improve?
 -Test Cases

## Getting started

To run the final server, build the jar:
```
./gradlew build
```

and run the server in Docker:

```
 docker-compose up --build
```

```
server is up on `http://localhost:9090`

## Development

```
  Run from main class  with any IDE
```

## Testing

For running unit test cases for frontend and backend modules
### Back end - configured with junit and mockito
```
./gradlew test
```
### Swagger UI
```
Once the server is up and running you and see the swagger ui on http://localhost:9090/swagger-ui/#/
```