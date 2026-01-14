#!/usr/bin/env bash
set -e

docker build -t shibakita/auth-service:1.0.0 ./services/auth-service
docker build -t shibakita/catalog-service:1.0.0 ./services/catalog-service
docker build -t shibakita/orders-service:1.0.0 ./services/orders-service

docker stack deploy -c ./infra/swarm/stack.yml shibakita

echo "Stack deployado!"
docker stack services shibakita
