# Shorty Pro on Minikube

This folder contains Kubernetes infrastructure to run the full Shorty Pro stack on Minikube:
- databases: `url-shortener-postgres`, `analytics-postgres`, `redis`
- kafka stack: `zookeeper`, `kafka`, `kafka-ui`
- apps: `url-shortener-service`, `analytics-service`
- observability: postgres exporters, kafka exporter, `prometheus`, `loki`, `grafana`, `redisinsight`

## 1) Start Minikube

```bash
minikube start --cpus=4 --memory=8192
```

## 2) Build app images into Minikube

Run from repository root (`/Users/qnocks/IdeaProjects/shorty-pro`):

```bash
minikube image build -t shorty/url-shortener-service:dev -f url-shortener-service/Dockerfile .
minikube image build -t shorty/analytics-service:dev -f analytics-service/Dockerfile .
```

```bash
minikube service -n shorty url-shortener-service --url
minikube service -n shorty analytics-service --url
    minikube service -n shortyasd## Notes.,mnfws uyfvsdjdbnkjsndxcjsaxfcjkasdvgha bjvhg˜∫√nsabled in app deployments for local stability.
- If a pod is not ready, inspect with:
a
kubectl -n shorty describe pod <pod-name>
kubectl -n shorty logs <pod-name>
```
