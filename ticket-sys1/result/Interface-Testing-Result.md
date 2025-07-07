## API 请求示例

### 用户注册
```bash
curl http://localhost:5000/register -H "Content-Type: application/json" -d '{"username": "alice", "password": "123"}'
```
响应:
```json
{
  "msg": "registered"
}
```

### 用户登录
```bash
curl -X POST localhost:5000/login -H "Content-Type: application/json" -d '{"username": "alice", "password": "123"}'
```
响应:
```json
{
  "msg": "login success",
  "uid": 1
}
```

### 获取车票信息
```bash
curl localhost:5000/tickets
```
响应:
```json
[
  {
    "available": 10,
    "id": 1,
    "name": "Train A - 08:00"
  }
]
```

### 车票预订
```bash
curl -X POST localhost:5000/book -H "Content-Type: application/json" -d '{"ticket_id": 1, "user_id": 1}'
```
响应:
```json
{
  "msg": "queued for booking"
}
```

### 查看订单
```bash
curl localhost:5000/orders/1
```
响应:
```json
[
  {
    "created_at": "Mon, 07 Jul 2025 22:16:36 GMT",
    "id": 1,
    "ticket_id": 1,
    "user_id": 1
  },
  {
    "created_at": "Mon, 07 Jul 2025 22:24:02 GMT",
    "id": 2,
    "ticket_id": 1,
    "user_id": 1
  }
]
```
