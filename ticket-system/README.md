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

```sql
-- 插入车站数据（Beijing, Shanghai, Guangzhou 各两个）
-- 插入车站数据
INSERT INTO public.entitystation (id, name, location) VALUES
(1, 'Beijing Railway Station', 'beijing'),
(2, 'Beijing West Railway Station', 'beijing'),
(3, 'Shanghai Railway Station', 'shanghai'),
(4, 'Shanghai Hongqiao Railway Station', 'shanghai'),
(5, 'Guangzhou Railway Station', 'guangzhou'),
(6, 'Guangzhou South Railway Station', 'guangzhou');

-- 插入列车数据
INSERT INTO public.entitytrain (id, seats) VALUES
(1, 100),
(2, 100),
(3, 100),
(4, 100),
(5, 100),
(6, 100),
(7, 100),
(8, 100),
(9, 100),
(10, 100);

-- 插入线路数据（随机组合车站，共10条以内）
INSERT INTO public.entityroute (id, distance, startstationid, endstationid) VALUES
(1, 1200, 1, 3),
(2, 2100, 2, 5),
(3, 1500, 4, 6),
(4, 2300, 1, 6),
(5, 1800, 3, 5),
(6, 1500, 6, 4),
(7, 1200, 2, 3),
(8, 300, 4, 3),
(9, 1400, 5, 6),
(10, 120, 1, 2);

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
