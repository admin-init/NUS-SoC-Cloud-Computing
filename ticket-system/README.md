# Prerequisite

- Install Java JDK 17+
- [Install Quarkus](https://quarkus.io/get-started/)
- [Install Maven](https://maven.apache.org/install.html)
- minikube
- docker
- PostgreSQL
	Run following command to run a PostgreSQL container.
	```bash
	docker run --name ticket-postgres   -e POSTGRES_DB=ticketdb   -e POSTGRES_USER=postgres   -e POSTGRES_PASSWORD=yourpassword   -p 5433:5432   -d postgres
	``` 
	> DO NOT change the password as the default password in application.properties is typically yourpassword :).
---
