#!/bin/bash
DOCKER_PLATFORMS='linux/amd64,linux/arm64'
registry='localhost:5000/'     # e.g. 'descartesresearch/'

print_usage() {
  printf "Usage: docker_build.sh [-p] [-r REGISTRY_NAME]\n"
}

while getopts 'pr:' flag; do
  case "${flag}" in
    p) push_flag='true' ;;
    r) registry="${OPTARG}" ;;
    *) print_usage
       exit 1 ;;
  esac
done

# buildx can not write images locally, so we spin up a local registry as a workaround, see
# https://github.com/docker/buildx/issues/301#issuecomment-755164475
docker run -d --name registry --network=host registry:2
docker run -it --rm --privileged tonistiigi/binfmt --install all
docker buildx create --use --name mybuilder --driver-opt network=host

perl -i -pe's|.*FROM descartesresearch/|FROM '"${registry}"'|g' ../services/tools.descartes.teastore.*/Dockerfile

docker buildx build --network=host --platform ${DOCKER_PLATFORMS} -t "${registry}teastore-db" ../utilities/tools.descartes.teastore.database/ --push
docker buildx build --network=host --platform ${DOCKER_PLATFORMS} -t "${registry}teastore-kieker-rabbitmq" ../utilities/tools.descartes.teastore.kieker.rabbitmq/ --push
docker buildx build --network=host --platform ${DOCKER_PLATFORMS} -t "${registry}teastore-base" ../utilities/tools.descartes.teastore.dockerbase/ --push
docker buildx build --network=host --platform ${DOCKER_PLATFORMS} -t "${registry}teastore-registry" ../services/tools.descartes.teastore.registry/ --push
docker buildx build --network=host --platform ${DOCKER_PLATFORMS} -t "${registry}teastore-persistence" ../services/tools.descartes.teastore.persistence/ --push
docker buildx build --network=host --platform ${DOCKER_PLATFORMS} -t "${registry}teastore-image" ../services/tools.descartes.teastore.image/ --push
docker buildx build --network=host --platform ${DOCKER_PLATFORMS} -t "${registry}teastore-webui" ../services/tools.descartes.teastore.webui/ --push
docker buildx build --network=host --platform ${DOCKER_PLATFORMS} -t "${registry}teastore-auth" ../services/tools.descartes.teastore.auth/ --push
docker buildx build --network=host --platform ${DOCKER_PLATFORMS} -t "${registry}teastore-recommender" ../services/tools.descartes.teastore.recommender/ --push

perl -i -pe's|.*FROM '"${registry}"'|FROM descartesresearch/|g' ../services/tools.descartes.teastore.*/Dockerfile

docker pull ${registry}teastore-db
docker image tag ${registry}teastore-db:latest teastore-db:latest
docker pull ${registry}teastore-kieker-rabbitmq
docker image tag ${registry}teastore-kieker-rabbitmq:latest teastore-kieker-rabbitmq:latest
docker pull ${registry}teastore-base
docker image tag ${registry}teastore-base:latest teastore-base:latest
docker pull ${registry}teastore-registry
docker image tag ${registry}teastore-registry:latest teastore-registry:latest
docker pull ${registry}teastore-persistence
docker image tag ${registry}teastore-persistence:latest teastore-persistence:latest
docker pull ${registry}teastore-image
docker image tag ${registry}teastore-image:latest teastore-image:latest
docker pull ${registry}teastore-webui
docker image tag ${registry}teastore-webui:latest teastore-webui:latest
docker pull ${registry}teastore-auth
docker image tag ${registry}teastore-auth:latest teastore-auth:latest
docker pull ${registry}teastore-recommender
docker image tag ${registry}teastore-recommender:latest teastore-recommender:latest

docker buildx rm mybuilder
docker rm -f $(docker ps -a -q)