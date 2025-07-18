# Kubenetes Front-end

## Delpoy Front-end

First `cd kubernetes/front-end`
```bash
kubectl apply -f vue-deploy.yaml
```

## Delpoy ingress

First `cd kubernetes/back-end/configs/ingress`.
```bash
kubectl apply -f ingress-nginx-front-end.yaml
```