DOCKER_COMPOSE = docker-compose

start:
	$(DOCKER_COMPOSE) up -d

stop:
	$(DOCKER_COMPOSE) down

clean: stop
	$(DOCKER_COMPOSE) rm -f
	docker volume rm $$(docker volume ls -qf dangling=true) 2>/dev/null || true
	rm -rf ./url-shortener-service/build

logs:
	$(DOCKER_COMPOSE) logs -f --tail=200
