要在 AWS 上实现基于 Redis 队列长度或 CPU 使用率的自动扩容机制，你有两个主流方案可以选择：

✅ 方案一：KEDA（推荐）
适合根据 Redis 队列长度等“应用层指标”来扩缩容，适配抢票系统的“排队抢单”场景。

🌟 一、安装 KEDA 到 AWS EKS
添加 Helm 仓库并安装 KEDA 控制器：
```bash
helm repo add kedacore https://kedacore.github.io/charts
helm repo update


helm install keda kedacore/keda --namespace keda --create-namespace
```

🌟 二、部署 Redis（你已有的话可跳过）
```bash
helm install redis bitnami/redis --set architecture=standalone
```
确保你有一个 Redis 地址，比如：
redis-master.keda.svc.cluster.local:6379

🌟 三、创建 ScaledObject 配置（监听 Redis 队列长度）
假设你要扩容的是一个 booking-worker Deployment，它监听 bookQueue 队列：

```yaml

apiVersion: keda.sh/v1alpha1
kind: ScaledObject
metadata:
  name: booking-worker-scaler
  namespace: default
spec:
  scaleTargetRef:
    name: booking-worker       # Deployment 名
  pollingInterval: 15          # 每 15 秒检查一次
  cooldownPeriod:  60          # 多久后缩容
  minReplicaCount: 1
  maxReplicaCount: 10
  triggers:
    - type: redis
      metadata:
        address: redis-master.keda.svc.cluster.local:6379
        passwordFromEnv: REDIS_PASSWORD
        listName: bookQueue
        listLength: "10"       # 超过 10 条就扩容
```
passwordFromEnv 会从 Pod 的环境变量中拉取 Redis 密码，你可以用 Secret 注入。

🌟 四、给 Deployment 加 Redis 密码 env（可选）
```yaml
env:
  - name: REDIS_PASSWORD
    valueFrom:
      secretKeyRef:
        name: redis
        key: redis-password
```

🌟 五、部署后验证
```bash
kubectl get scaledobject
kubectl get hpa
kubectl get deployment booking-worker
kubectl get pods -| app=booking-worker
```
你可以用 redis-cli 或应用代码往 bookQueue 写入元素，然后查看是否触发扩容。


✅ 正确使用 redis-cli 推入队列
执行以下命令登录并推入任务：
```bash

kubectl exec -it redis-master-0 -- redis-cli
```
在 redis-cli 交互界面中：

```redis

AUTH redispass
LPUSH bookQueue "task1"
LPUSH bookQueue "task2"
...
LPUSH bookQueue "task11"
```
确保 至少推入 11 条，因为 ScaledObject 配置里是：
listLength: "10"

✅ 然后观察扩容是否触发
```bash
kubectl get scaledobject
kubectl get hpa
kubectl get deployment booking-worker
kubectl get pods -l app=booking-worker
```
你应该看到：
ACTIVE = True
REPLICAS 增加（从 1 扩到 >1）
新 Pod 正在创建中



default 命名空间下的 default ServiceAccount 没有足够的权限来列出 pods 资源。

解决方案：
您需要为 default ServiceAccount 分配适当的权限，以允许它列出 pods 和 podsets 等资源。可以通过创建一个 Role 和 RoleBinding 或 ClusterRole 和 ClusterRoleBinding 来授予权限。

1. 创建 ClusterRole 和 ClusterRoleBinding
```yaml
    apiVersion: rbac.authorization.k8s.io/v1
    kind: ClusterRole
    metadata:
    # 可以根据需要更改名称
    # 创建一个 ClusterRole，允许访问 Pods 和 PodSets
    name: pod-access-role
    rules:
    - apiGroups: [""]
        resources: ["pods"]
        verbs: ["get", "list", "watch"]
    - apiGroups: ["apps.example.com"]
        resources: ["podsets"]
        verbs: ["get", "list", "watch"]
    ---
    apiVersion: rbac.authorization.k8s.io/v1
    kind: ClusterRoleBinding
    metadata:
    name: pod-access-binding
    subjects:
    - kind: ServiceAccount
        name: default
        namespace: default
    roleRef:
    kind: ClusterRole
    name: pod-access-role
    apiGroup: rbac.authorization.k8s.io
```
2. 应用这个配置
将配置文件保存为 pod-access-role.yaml，然后执行以下命令：

```bash
kubectl apply -f pod-access-role.yaml
```

修复权限问题的完整 YAML 文件
保存为：booking-worker-access.yaml

```yaml

apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRole
metadata:
  name: booking-worker-pod-access
rules:
  - apiGroups: [""]
    resources: ["pods"]
    verbs: ["get", "list", "watch"]
  - apiGroups: ["apps.example.com"]
    resources: ["podsets"]
    verbs: ["get", "list", "watch"]
---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  name: booking-worker-pod-access-binding
subjects:
  - kind: ServiceAccount
    name: default
    namespace: default
roleRef:
  kind: ClusterRole
  name: booking-worker-pod-access
  apiGroup: rbac.authorization.k8s.io
```



