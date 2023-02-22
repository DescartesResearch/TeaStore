mvn package -DskipTests

docker build -t descartesresearch/teastore-auth:latest services/tools.descartes.teastore.auth
docker build -t descartesresearch/teastore-persistence:latest services/tools.descartes.teastore.persistence
docker build -t descartesresearch/teastore-registry:latest services/tools.descartes.teastore.registry
docker build -t descartesresearch/teastore-image:latest services/tools.descartes.teastore.image
docker build -t descartesresearch/teastore-recommender:latest services/tools.descartes.teastore.recommender
docker build -t descartesresearch/teastore-webui:latest services/tools.descartes.teastore.webui

docker compose -f ./examples/docker/docker-compose_default.yaml up -d
