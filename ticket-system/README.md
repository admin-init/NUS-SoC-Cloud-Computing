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

```bash
quarkus build --native -Dquarkus.native.container-build=true -Dquarkus.native.container-runtime=docker
```
```bash
eval $(minikube docker-env)
docker build -f src/main/docker/Dockerfile.native-micro -t ticket-system/<service> .
```

---

# Database
```bash
psql -h localhost -p 5433 -U postgres -d ticketdb
```
## Get schema

```bash
# 进入容器
docker exec -it ticket-postgres bash

# 在容器内执行 pg_dump（导出 public schema）
pg_dump -U postgres -d ticketdb --schema=public -s -f /tmp/public_schema.sql

# 退出容器
exit

# 将文件从容器拷贝到本机当前目录
docker cp ticket-postgres:/tmp/public_schema.sql ./public_schema.sql
```

## Insert Test Data

- [DB init of operations-service](./operations-management-service/src/main/resources/import.sql)
- [DB init of order-srvice](./order-management-service/src/main/resources/import.sql)

---

```bash
psql -h localhost -p 5433 -U postgres -d ticketdb -f Download/seed_data.sql
```

# Project Creation

## ticket-management-service

```bash
quarkus create app com.example.ticket-management-service:ticket-management-service \
    --extension=quarkus-rest,quarkus-rest-jackson,quarkus-hibernate-orm-panache,quarkus-smallrye-openapi,quarkus-jdbc-postgresql
```
---
