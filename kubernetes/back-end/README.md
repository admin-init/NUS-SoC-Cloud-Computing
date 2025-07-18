# Kubenetes Back-end

## Deploy Database

### Deploy with Statefulset

Fisrt `cd kubernetes/back-end/configs/postgre`,

then apply the yaml:
```bash
kubectl apply -f postgres-statefulset.yaml
```
### Deploy with CloudnativePG operator

First `cd cd kubernetes/back-end/helm3/postgres`
```bash
helm repo add cnpg https://cloudnative-pg.github.io/charts
helm repo update
```

then with [Single namespace installation](https://github.com/cloudnative-pg/charts?tab=readme-ov-file#single-namespace-installation):
```bash
helm upgrade --install cnpg \
  --namespace cnpg-system \
  --create-namespace \
  --set config.clusterWide=false \
  cnpg/cloudnative-pg
```

now we can apply cluster:
```bash
kubectl apply -f p postgres-ticketdb-cluster.yaml
```

## Use client-go to deploy microservices

- DB deploy method: CloudnativePG operators 
    - `cd kubernetes/back-end/client-go/k8s-deployer`
- DB delpoy method: Statefulset 
    - `cd kubernetes/back-end/client-go/k8s-deployer-origin`

```bash
go build -x -o k8s-deployer main.go
```
create using `--action=create`:
```bash
go run main.go --action=create
```
delete using `--action=delete`:
```bash
go run main.go --action=delete
```

## helm3

### Install Prometheus

To install prometheus-community using helm3, first `cd kubernetes/back-end/helm3/promethesus`.

then add repository:
```bash
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update
```

then install into namespace monitoring with customed `values.yaml`:
```bash
helm install kube-prometheus-stack prometheus-community/kube-prometheus-stack \
  --namespace monitoring \
  --create-namespace
```

you should see something like this:
```bash
NAME: kube-prometheus-stack
LAST DEPLOYED: Mon Jul 14 21:54:03 2025
NAMESPACE: monitoring
STATUS: deployed
REVISION: 1
NOTES:
kube-prometheus-stack has been installed. Check its status by running:
  kubectl --namespace monitoring get pods -l "release=kube-prometheus-stack"

Get Grafana 'admin' user password by running:

  kubectl --namespace monitoring get secrets kube-prometheus-stack-grafana -o jsonpath="{.data.admin-password}" | base64 -d ; echo

Access Grafana local instance:

  export POD_NAME=$(kubectl --namespace monitoring get pod -l "app.kubernetes.io/name=grafana,app.kubernetes.io/instance=kube-prometheus-stack" -oname)
  kubectl --namespace monitoring port-forward $POD_NAME 3000

Visit https://github.com/prometheus-operator/kube-prometheus for instructions on how to create & configure Alertmanager and Prometheus instances using the Operator.
```

You can always adapt new `values.yaml` using `helm upgrade`, for example:
```bash
helm upgrade -n monitoring kube-prometheus-stack prometheus-community/kube-prometheus-stack -f custom-values.yaml
```

### Install ingress nginx

To install ingress nginx using helm3, first `cd kubernetes/back-end/helm3/ingress-nginx`.
```bash
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo update
```

then install into namespace ingress-nginx with customed `values.yaml`:
```bash
helm install ingress-nginx ingress-nginx/ingress-nginx \
  --namespace ingress-nginx \
  --create-namespace \
  -f ./custom-values.yaml
```

You can always adapt new `values.yaml` using `helm upgrade`, for example:
```bash
helm upgrade ingress-nginx ingress-nginx/ingress-nginx \
  --namespace ingress-nginx \
  -f ./custom-values.yaml
```

### Install KEDA

To install ingress nginx using helm3, first `cd kubernetes/back-end/helm3/KEDA`.
```bash
helm repo add kedacore https://kedacore.github.io/charts  
helm repo update
```

then install into namespace ingress-nginx with customed `values.yaml`:
```bash
helm install keda kedacore/keda --namespace keda --create-namespace -f custom-values.yaml
```