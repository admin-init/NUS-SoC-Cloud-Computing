# Prerequisite

- JDK 17+ installed with `JAVA_HOME` configured appropriately
- [Install Quarkus](https://quarkus.io/get-started/)
- [Install Maven](https://maven.apache.org/install.html)
- minikube
- docker
- PostgreSQL
	
	Run following command to run a PostgreSQL container.
	```bash
	docker run --name ticket-postgres   -e POSTGRES_DB=ticketdb   -e POSTGRES_USER=postgres   -e POSTGRES_PASSWORD=yourpassword   -p 5433:5432   -d postgres
	``` 
	> DO NOT change the password as the default password in `application.properties` is literally `yourpassword` :).
---

# Build Project

## Operations Management Service

### Live develpoment mode

Use following command to open live develop:
```bash
quarkus dev
```
Then you can access the [web dev workspace](http:\\localhost:8080) to see configurations.
And also access the [openapi swagger-ui webpage](http://localhost:8080/q/swagger-ui/) to do simple testing.

### Build


---

# Project Creation

## ticket-management-service

```bash
quarkus create app com.example.ticket-management-service:ticket-management-service \
    --extension=quarkus-rest,quarkus-rest-jackson,quarkus-hibernate-orm-panache,quarkus-smallrye-openapi,quarkus-jdbc-postgresql
```
---
