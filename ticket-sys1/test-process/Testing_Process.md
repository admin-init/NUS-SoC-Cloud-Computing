## 二、初始化步骤（开发环境）

### 1. 启动本地 Kubernetes (Minikube)

```bash
minikube start
```
```bash
minikube addons enable ingress # 确保启用了ingress
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.8.2/deploy/static/provider/cloud/deploy.yaml # 以及确保有可用的ingressClass
```

### 2. 启动 Minikube 内部 Docker 环境（打包 image 到其中）

```bash
eval $(minikube docker-env)
```

### 3. 构建 Flask 应用镜像

在项目根目录中执行：

```bash
docker build -t ticket-app:latest -f Dockerfile .
```

这样你本地的 `ticket-app:latest` 镜像就可以被 Minikube 使用。

## 三、部署数据库和缓存服务

在 `ticketing-system/k8s/` 目录下执行：

```bash
kubectl apply -f mysql.yaml
kubectl apply -f redis.yaml
```

验证是否运行成功：

```bash
kubectl get pods
```

## 四、部署后端应用服务和 Ingress

```bash
kubectl apply -f deployment-app.yaml
kubectl apply -f ingress.yaml
```

## 五、配置 Ingress 域名解析（仅本地）

### 获取 Minikube IP

```bash
minikube ip
```

假设输出为 `192.168.49.2`，将其添加到你的 `hosts` 文件中：

```text
192.168.49.2 ticket.local
```

- **Linux/macOS 编辑** `/etc/hosts`
- **Windows 编辑** `C:\Windows\System32\drivers\etc\hosts`

## 六、初始化数据库（执行 SQL 表结构）

### 进入 MySQL 容器

```bash
kubectl exec -it $(kubectl get pods | grep mysql | awk '{print $1}') -- bash
mysql -u root -p
# 输入密码：123456
CREATE DATABASE ticketdb;
USE ticketdb;
```

### 然后运行以下 SQL 结构

```sql
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255),
    password VARCHAR(255)
);

CREATE TABLE tickets (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    available INT
);

CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    ticket_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 然后你可以手动插入测试票

```sql
INSERT INTO tickets (name, available) VALUES ('Train A - 08:00', 10);
```
```sql
SELECT * FROM tickets; # 检查插入是否成功
```
## 七、测试接口功能（Postman/curl）

### 注册用户

```bash
curl -X POST ticket.local/register -H "Content-Type: application/json" -d '{"username": "alice", "password": "123"}'
```

### 登录获取 UID

```bash
curl -X POST ticket.local/login -H "Content-Type: application/json" -d '{"username": "alice", "password": "123"}'
```

### 查询票

```bash
curl ticket.local/tickets
```

### 抢票（用返回的 user_id 和 ticket_id）

```bash
curl -X POST ticket.local/book -H "Content-Type: application/json" -d '{"ticket_id": 1, "user_id": 1}'
```

### 查看订单

```bash
curl ticket.local/orders/1
```
