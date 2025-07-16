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

# Kubenetes

## client-go

```bash
go run main.go --action=create
```
```bash
go run main.go --action=delete
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

```sql
-- This file allow to write SQL commands that will be emitted in test and dev.
-- The commands are commented as their support depends of the database
-- insert into myentity (id, field) values(1, 'field-1');
-- insert into myentity (id, field) values(2, 'field-2');
-- insert into myentity (id, field) values(3, 'field-3');
-- alter sequence myentity_seq restart with 4;

-- entitystation
CREATE TABLE IF NOT EXISTS public.entitystation (
    id BIGSERIAL PRIMARY KEY,
    location VARCHAR(255),
    name VARCHAR(255)
);

-- entityroute
CREATE TABLE IF NOT EXISTS public.entityroute (
    id BIGSERIAL PRIMARY KEY,
    distance INTEGER,
    endstationid BIGINT REFERENCES public.entitystation(id),
    startstationid BIGINT REFERENCES public.entitystation(id)
);

-- entitytrain
CREATE TABLE IF NOT EXISTS public.entitytrain (
    id BIGSERIAL PRIMARY KEY,
    seats INTEGER
);

-- entityschedule
CREATE TABLE IF NOT EXISTS public.entityschedule (
    id BIGSERIAL PRIMARY KEY,
    arrivaltime TIMESTAMP(6) WITHOUT TIME ZONE,
    departuretime TIMESTAMP(6) WITHOUT TIME ZONE,
    routeid BIGINT REFERENCES public.entityroute(id),
    trainid BIGINT REFERENCES public.entitytrain(id),
    schedulecode VARCHAR(255)
);

-- entityticket
CREATE TABLE IF NOT EXISTS public.entityticket (
    id BIGSERIAL PRIMARY KEY,
    amount INTEGER,
    availableamount INTEGER,
    price DOUBLE PRECISION,
    scheduleid BIGINT REFERENCES public.entityschedule(id)
);

-- entityadmin
CREATE TABLE IF NOT EXISTS public.entityadmin (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    password VARCHAR(255)
);

-- entityuser
CREATE TABLE IF NOT EXISTS public.entityuser (
    id BIGSERIAL PRIMARY KEY,
    contact VARCHAR(255),
    identitycode VARCHAR(255),
    name VARCHAR(255),
    password VARCHAR(255)
);

-- entityorder
CREATE TABLE IF NOT EXISTS public.entityorder (
    id BIGSERIAL PRIMARY KEY,
    price DOUBLE PRECISION,
    scheduleid INTEGER,
    seatcode INTEGER,
    ticketid INTEGER,
    userid INTEGER,
    orderdate TIMESTAMP(6) WITHOUT TIME ZONE,
    status VARCHAR(255)
);

-- Insert Station info
INSERT INTO public.entitystation (name, location) VALUES
('Beijing Railway Station', 'beijing'),
('Beijing West Railway Station', 'beijing'),
('Shanghai Railway Station', 'shanghai'),
('Shanghai Hongqiao Railway Station', 'shanghai'),
('Guangzhou Railway Station', 'guangzhou'),
('Guangzhou South Railway Station', 'guangzhou');

-- Insert Train info
INSERT INTO public.entitytrain (seats) VALUES
(90),
(90),
(90),
(90),
(90),
(90),
(90),
(120),
(120),
(120);

-- Insert Route info
INSERT INTO public.entityroute (distance, startstationid, endstationid) VALUES
(1200, 1, 3),    -- 北京站 -> 上海站
(1220, 1, 4),    -- 北京站 -> 上海虹桥站
(1450, 2, 5),    -- 北京西站 -> 广州站
(1470, 2, 6),    -- 北京西站 -> 广州南站
(1500, 3, 5),    -- 上海站 -> 广州站
(1520, 4, 6);    -- 上海虹桥站 -> 广州南站

-- Insert Schedule info
INSERT INTO public.entityschedule (arrivaltime, departuretime, schedulecode, routeid, trainid) VALUES
( '2025-07-12 09:00:00', '2025-07-12 08:00', 'R1_20250712_0800', 1, 2),
( '2025-07-12 13:00:00', '2025-07-12 12:00', 'R1_20250712_1200', 1, 3),
( '2025-07-12 17:00:00', '2025-07-12 16:00', 'R1_20250712_1600', 1, 4),
( '2025-07-13 09:00:00', '2025-07-13 08:00', 'R1_20250713_0800', 1, 5),
( '2025-07-13 13:00:00', '2025-07-13 12:00', 'R1_20250713_1200', 1, 6),
( '2025-07-13 17:00:00', '2025-07-13 16:00', 'R1_20250713_1600', 1, 7),
( '2025-07-14 09:00:00', '2025-07-14 08:00', 'R1_20250714_0800', 1, 8),
( '2025-07-14 13:00:00', '2025-07-14 12:00', 'R1_20250714_1200', 1, 9),
( '2025-07-14 17:00:00', '2025-07-14 16:00', 'R1_20250714_1600', 1, 10),
( '2025-07-15 09:00:00', '2025-07-15 08:00', 'R1_20250715_0800', 1, 1),
( '2025-07-15 13:00:00', '2025-07-15 12:00', 'R1_20250715_1200', 1, 2),
( '2025-07-15 17:00:00', '2025-07-15 16:00', 'R1_20250715_1600', 1, 3),
( '2025-07-16 09:00:00', '2025-07-16 08:00', 'R1_20250716_0800', 1, 4),
( '2025-07-16 13:00:00', '2025-07-16 12:00', 'R1_20250716_1200', 1, 5),
( '2025-07-16 17:00:00', '2025-07-16 16:00', 'R1_20250716_1600', 1, 6),
( '2025-07-17 09:00:00', '2025-07-17 08:00', 'R1_20250717_0800', 1, 7),
( '2025-07-17 13:00:00', '2025-07-17 12:00', 'R1_20250717_1200', 1, 8),
( '2025-07-17 17:00:00', '2025-07-17 16:00', 'R1_20250717_1600', 1, 9),
( '2025-07-18 09:00:00', '2025-07-18 08:00', 'R1_20250718_0800', 1, 10),
( '2025-07-18 13:00:00', '2025-07-18 12:00', 'R1_20250718_1200', 1, 1),
( '2025-07-18 17:00:00', '2025-07-18 16:00', 'R1_20250718_1600', 1, 2),
( '2025-07-12 09:00:00', '2025-07-12 08:00', 'R2_20250712_0800', 2, 3),
( '2025-07-12 13:00:00', '2025-07-12 12:00', 'R2_20250712_1200', 2, 4),
( '2025-07-12 17:00:00', '2025-07-12 16:00', 'R2_20250712_1600', 2, 5),
( '2025-07-13 09:00:00', '2025-07-13 08:00', 'R2_20250713_0800', 2, 6),
( '2025-07-13 13:00:00', '2025-07-13 12:00', 'R2_20250713_1200', 2, 7),
( '2025-07-13 17:00:00', '2025-07-13 16:00', 'R2_20250713_1600', 2, 8),
( '2025-07-14 09:00:00', '2025-07-14 08:00', 'R2_20250714_0800', 2, 9),
( '2025-07-14 13:00:00', '2025-07-14 12:00', 'R2_20250714_1200', 2, 10),
( '2025-07-14 17:00:00', '2025-07-14 16:00', 'R2_20250714_1600', 2, 1),
( '2025-07-15 09:00:00', '2025-07-15 08:00', 'R2_20250715_0800', 2, 2),
( '2025-07-15 13:00:00', '2025-07-15 12:00', 'R2_20250715_1200', 2, 3),
( '2025-07-15 17:00:00', '2025-07-15 16:00', 'R2_20250715_1600', 2, 4),
( '2025-07-16 09:00:00', '2025-07-16 08:00', 'R2_20250716_0800', 2, 5),
( '2025-07-16 13:00:00', '2025-07-16 12:00', 'R2_20250716_1200', 2, 6),
( '2025-07-16 17:00:00', '2025-07-16 16:00', 'R2_20250716_1600', 2, 7),
( '2025-07-17 09:00:00', '2025-07-17 08:00', 'R2_20250717_0800', 2, 8),
( '2025-07-17 13:00:00', '2025-07-17 12:00', 'R2_20250717_1200', 2, 9),
( '2025-07-17 17:00:00', '2025-07-17 16:00', 'R2_20250717_1600', 2, 10),
( '2025-07-18 09:00:00', '2025-07-18 08:00', 'R2_20250718_0800', 2, 1),
( '2025-07-18 13:00:00', '2025-07-18 12:00', 'R2_20250718_1200', 2, 2),
( '2025-07-18 17:00:00', '2025-07-18 16:00', 'R2_20250718_1600', 2, 3),
( '2025-07-12 08:00:00', '2025-07-12 08:00', 'R3_20250712_0800', 3, 4),
( '2025-07-12 12:00:00', '2025-07-12 12:00', 'R3_20250712_1200', 3, 5),
( '2025-07-12 16:00:00', '2025-07-12 16:00', 'R3_20250712_1600', 3, 6),
( '2025-07-13 08:00:00', '2025-07-13 08:00', 'R3_20250713_0800', 3, 7),
( '2025-07-13 12:00:00', '2025-07-13 12:00', 'R3_20250713_1200', 3, 8),
( '2025-07-13 16:00:00', '2025-07-13 16:00', 'R3_20250713_1600', 3, 9),
( '2025-07-14 08:00:00', '2025-07-14 08:00', 'R3_20250714_0800', 3, 10),
( '2025-07-14 12:00:00', '2025-07-14 12:00', 'R3_20250714_1200', 3, 1),
( '2025-07-14 16:00:00', '2025-07-14 16:00', 'R3_20250714_1600', 3, 2),
( '2025-07-15 08:00:00', '2025-07-15 08:00', 'R3_20250715_0800', 3, 3),
( '2025-07-15 12:00:00', '2025-07-15 12:00', 'R3_20250715_1200', 3, 4),
( '2025-07-15 16:00:00', '2025-07-15 16:00', 'R3_20250715_1600', 3, 5),
( '2025-07-16 08:00:00', '2025-07-16 08:00', 'R3_20250716_0800', 3, 6),
( '2025-07-16 12:00:00', '2025-07-16 12:00', 'R3_20250716_1200', 3, 7),
( '2025-07-16 16:00:00', '2025-07-16 16:00', 'R3_20250716_1600', 3, 8),
( '2025-07-17 08:00:00', '2025-07-17 08:00', 'R3_20250717_0800', 3, 9),
( '2025-07-17 12:00:00', '2025-07-17 12:00', 'R3_20250717_1200', 3, 10),
( '2025-07-17 16:00:00', '2025-07-17 16:00', 'R3_20250717_1600', 3, 1),
( '2025-07-18 08:00:00', '2025-07-18 08:00', 'R3_20250718_0800', 3, 2),
( '2025-07-18 12:00:00', '2025-07-18 12:00', 'R3_20250718_1200', 3, 3),
( '2025-07-18 16:00:00', '2025-07-18 16:00', 'R3_20250718_1600', 3, 4),
( '2025-07-12 08:00:00', '2025-07-12 08:00', 'R4_20250712_0800', 4, 5),
( '2025-07-12 12:00:00', '2025-07-12 12:00', 'R4_20250712_1200', 4, 6),
( '2025-07-12 16:00:00', '2025-07-12 16:00', 'R4_20250712_1600', 4, 7),
( '2025-07-13 08:00:00', '2025-07-13 08:00', 'R4_20250713_0800', 4, 8),
( '2025-07-13 12:00:00', '2025-07-13 12:00', 'R4_20250713_1200', 4, 9),
( '2025-07-13 16:00:00', '2025-07-13 16:00', 'R4_20250713_1600', 4, 10),
( '2025-07-14 08:00:00', '2025-07-14 08:00', 'R4_20250714_0800', 4, 1),
( '2025-07-14 12:00:00', '2025-07-14 12:00', 'R4_20250714_1200', 4, 2),
( '2025-07-14 16:00:00', '2025-07-14 16:00', 'R4_20250714_1600', 4, 3),
( '2025-07-15 08:00:00', '2025-07-15 08:00', 'R4_20250715_0800', 4, 4),
( '2025-07-15 12:00:00', '2025-07-15 12:00', 'R4_20250715_1200', 4, 5),
( '2025-07-15 16:00:00', '2025-07-15 16:00', 'R4_20250715_1600', 4, 6),
( '2025-07-16 08:00:00', '2025-07-16 08:00', 'R4_20250716_0800', 4, 7),
( '2025-07-16 12:00:00', '2025-07-16 12:00', 'R4_20250716_1200', 4, 8),
( '2025-07-16 16:00:00', '2025-07-16 16:00', 'R4_20250716_1600', 4, 9),
( '2025-07-17 08:00:00', '2025-07-17 08:00', 'R4_20250717_0800', 4, 10),
( '2025-07-17 12:00:00', '2025-07-17 12:00', 'R4_20250717_1200', 4, 1),
( '2025-07-17 16:00:00', '2025-07-17 16:00', 'R4_20250717_1600', 4, 2),
( '2025-07-18 08:00:00', '2025-07-18 08:00', 'R4_20250718_0800', 4, 3),
( '2025-07-18 12:00:00', '2025-07-18 12:00', 'R4_20250718_1200', 4, 4),
( '2025-07-18 16:00:00', '2025-07-18 16:00', 'R4_20250718_1600', 4, 5),
( '2025-07-12 07:00:00', '2025-07-12 08:00', 'R5_20250712_0800', 5, 6),
( '2025-07-12 11:00:00', '2025-07-12 12:00', 'R5_20250712_1200', 5, 7),
( '2025-07-12 15:00:00', '2025-07-12 16:00', 'R5_20250712_1600', 5, 8),
( '2025-07-13 07:00:00', '2025-07-13 08:00', 'R5_20250713_0800', 5, 9),
( '2025-07-13 11:00:00', '2025-07-13 12:00', 'R5_20250713_1200', 5, 10),
( '2025-07-13 15:00:00', '2025-07-13 16:00', 'R5_20250713_1600', 5, 1),
( '2025-07-14 07:00:00', '2025-07-14 08:00', 'R5_20250714_0800', 5, 2),
( '2025-07-14 11:00:00', '2025-07-14 12:00', 'R5_20250714_1200', 5, 3),
( '2025-07-14 15:00:00', '2025-07-14 16:00', 'R5_20250714_1600', 5, 4),
( '2025-07-15 07:00:00', '2025-07-15 08:00', 'R5_20250715_0800', 5, 5),
( '2025-07-15 11:00:00', '2025-07-15 12:00', 'R5_20250715_1200', 5, 6),
( '2025-07-15 15:00:00', '2025-07-15 16:00', 'R5_20250715_1600', 5, 7),
( '2025-07-16 07:00:00', '2025-07-16 08:00', 'R5_20250716_0800', 5, 8),
( '2025-07-16 11:00:00', '2025-07-16 12:00', 'R5_20250716_1200', 5, 9),
( '2025-07-16 15:00:00', '2025-07-16 16:00', 'R5_20250716_1600', 5, 10),
( '2025-07-17 07:00:00', '2025-07-17 08:00', 'R5_20250717_0800', 5, 1),
( '2025-07-17 11:00:00', '2025-07-17 12:00', 'R5_20250717_1200', 5, 2),
( '2025-07-17 15:00:00', '2025-07-17 16:00', 'R5_20250717_1600', 5, 3),
( '2025-07-18 07:00:00', '2025-07-18 08:00', 'R5_20250718_0800', 5, 4),
( '2025-07-18 11:00:00', '2025-07-18 12:00', 'R5_20250718_1200', 5, 5),
( '2025-07-18 15:00:00', '2025-07-18 16:00', 'R5_20250718_1600', 5, 6),
( '2025-07-12 07:00:00', '2025-07-12 08:00', 'R6_20250712_0800', 6, 7),
( '2025-07-12 11:00:00', '2025-07-12 12:00', 'R6_20250712_1200', 6, 8),
( '2025-07-12 15:00:00', '2025-07-12 16:00', 'R6_20250712_1600', 6, 9),
( '2025-07-13 07:00:00', '2025-07-13 08:00', 'R6_20250713_0800', 6, 10),
( '2025-07-13 11:00:00', '2025-07-13 12:00', 'R6_20250713_1200', 6, 1),
( '2025-07-13 15:00:00', '2025-07-13 16:00', 'R6_20250713_1600', 6, 2),
( '2025-07-14 07:00:00', '2025-07-14 08:00', 'R6_20250714_0800', 6, 3),
( '2025-07-14 11:00:00', '2025-07-14 12:00', 'R6_20250714_1200', 6, 4),
( '2025-07-14 15:00:00', '2025-07-14 16:00', 'R6_20250714_1600', 6, 5),
( '2025-07-15 07:00:00', '2025-07-15 08:00', 'R6_20250715_0800', 6, 6),
( '2025-07-15 11:00:00', '2025-07-15 12:00', 'R6_20250715_1200', 6, 7),
( '2025-07-15 15:00:00', '2025-07-15 16:00', 'R6_20250715_1600', 6, 8),
( '2025-07-16 07:00:00', '2025-07-16 08:00', 'R6_20250716_0800', 6, 9),
( '2025-07-16 11:00:00', '2025-07-16 12:00', 'R6_20250716_1200', 6, 10),
( '2025-07-16 15:00:00', '2025-07-16 16:00', 'R6_20250716_1600', 6, 1),
( '2025-07-17 07:00:00', '2025-07-17 08:00', 'R6_20250717_0800', 6, 2),
( '2025-07-17 11:00:00', '2025-07-17 12:00', 'R6_20250717_1200', 6, 3),
( '2025-07-17 15:00:00', '2025-07-17 16:00', 'R6_20250717_1600', 6, 4),
( '2025-07-18 07:00:00', '2025-07-18 08:00', 'R6_20250718_0800', 6, 5),
( '2025-07-18 11:00:00', '2025-07-18 12:00', 'R6_20250718_1200', 6, 6),
( '2025-07-18 15:00:00', '2025-07-18 16:00', 'R6_20250718_1600', 6, 7);

INSERT INTO entityuser (name, identitycode, contact, password) VALUES
('John Smith', '110101199003072516', 'john.smith@example.com', 'password123'),
('Michael Johnson', '110101198506123456', 'michael.johnson@example.com', 'password123'),
('Christopher Williams', '110101198812123456', 'chris.williams@example.com', 'password123'),
('David Brown', '110101199501012233', 'david.brown@example.com', 'password123'),
('James Miller', '110101199202021122', 'james.miller@example.com', 'password123'),
('Robert Davis', '110101199303031234', 'robert.davis@example.com', 'password123'),
('Daniel Garcia', '110101199404045566', 'daniel.garcia@example.com', 'password123'),
('Matthew Martinez', '110101199605056677', 'matthew.martinez@example.com', 'password123'),
('Anthony Robinson', '110101199706067788', 'anthony.robinson@example.com', 'password123'),
('Kevin Clark', '110101199807078899', 'kevin.clark@example.com', 'password123');

INSERT INTO entityadmin (name, password) VALUES
('Admin John', 'adminpass1'),
('Admin Sarah', 'adminpass2'),
('Super Admin', 'superpass');

-- 用户 1 的订单
INSERT INTO entityorder (price, scheduleid, seatcode, ticketid, userid, orderdate, status) VALUES
(120, 1001, 15, 101, 1, '2025-07-01 10:00:00', 'finished'),
(120, 1002, 16, 102, 1, '2025-07-03 14:20:00', 'finished'),
(120, 1003, 17, 103, 1, '2025-07-05 09:15:00', 'finished');

-- 用户 2 的订单
INSERT INTO entityorder (price, scheduleid, seatcode, ticketid, userid, orderdate, status) VALUES
(130, 1004, 22, 104, 2, '2025-07-02 11:00:00', 'finished'),
(130, 1005, 23, 105, 2, '2025-07-04 16:45:00', 'finished'),
(130, 1006, 24, 106, 2, '2025-07-06 13:30:00', 'finished');

-- 用户 3 的订单
INSERT INTO entityorder (price, scheduleid, seatcode, ticketid, userid, orderdate, status) VALUES
(110, 1007, 31, 107, 3, '2025-07-01 15:00:00', 'finished'),
(110, 1008, 32, 108, 3, '2025-07-03 10:10:00', 'finished'),
(110, 1009, 33, 109, 3, '2025-07-07 08:45:00', 'finished');
```

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
