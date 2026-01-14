#!/usr/bin/env bash
set -e

docker swarm init || true
echo "Swarm pronto."
docker node ls
