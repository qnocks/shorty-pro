DOCKER_COMPOSE = docker-compose

# Service groups
DB_SERVICES = url-shortener-postgres analytics-postgres redis redisinsight
APP_SERVICES = url-shortener-service analytics-service
KAFKA_SERVICES = zookeeper kafka kafka-ui
INFRA_SERVICES = url-shortener-postgres-exporter analytics-postgres-exporter kafka-exporter prometheus loki grafana alloy

# Main commands
start:
	$(DOCKER_COMPOSE) up -d

build:
	$(DOCKER_COMPOSE) up -d --build

stop:
	$(DOCKER_COMPOSE) stop

down:
	$(DOCKER_COMPOSE) down

clean: down
	$(DOCKER_COMPOSE) rm -f
	docker volume rm $$(docker volume ls -qf dangling=true) 2>/dev/null || true
	rm -rf ./url-shortener-service/build

logs:
	$(DOCKER_COMPOSE) logs -f --tail=200

status:
	$(DOCKER_COMPOSE) ps


# Database group commands
dbs:
	$(DOCKER_COMPOSE) up -d $(DB_SERVICES)

dbs-logs:
	$(DOCKER_COMPOSE) logs -f $(DB_SERVICES) --tail=200

dbs-stop:
	$(DOCKER_COMPOSE) stop $(DB_SERVICES)


# Kafka group commands
kafka:
	$(DOCKER_COMPOSE) up -d $(KAFKA_SERVICES)

kafka-logs:
	$(DOCKER_COMPOSE) logs -f $(KAFKA_SERVICES) --tail=200

kafka-stop:
	$(DOCKER_COMPOSE) stop $(KAFKA_SERVICES)


# App service group commands
app:
	$(DOCKER_COMPOSE) up -d $(APP_SERVICES)

app-logs:
	$(DOCKER_COMPOSE) logs -f $(APP_SERVICES) --tail=200

app-stop:
	$(DOCKER_COMPOSE) stop $(APP_SERVICES)


# Infrastructure group commands
infra:
	$(DOCKER_COMPOSE) up -d $(INFRA_SERVICES)

infra-logs:
	$(DOCKER_COMPOSE) logs -f $(INFRA_SERVICES) --tail=200

infra-stop:
	$(DOCKER_COMPOSE) stop $(INFRA_SERVICES)
