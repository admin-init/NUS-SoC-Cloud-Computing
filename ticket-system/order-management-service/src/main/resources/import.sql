-- This file allow to write SQL commands that will be emitted in test and dev.
-- The commands are commented as their support depends of the database
-- insert into myentity (id, field) values(1, 'field-1');
-- insert into myentity (id, field) values(2, 'field-2');
-- insert into myentity (id, field) values(3, 'field-3');
-- alter sequence myentity_seq restart with 4;

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