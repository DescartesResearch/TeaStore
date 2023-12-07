#!/bin/bash
DOCKER_PLATFORMS='linux/amd64,linux/arm64'
registry=''     # e.g. 'descartesresearch/'

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

if [ $DEPLOY == 1 ]
then
	docker run -it --rm --privileged tonistiigi/binfmt --install all
	docker buildx create --use --name mybuilder
	docker buildx build --platform ${DOCKER_PLATFORMS} -t "${registry}teastore-db" ../utilities/tools.descartes.teastore.database/ --push
	docker buildx build --platform ${DOCKER_PLATFORMS} -t "${registry}teastore-kieker-rabbitmq" ../utilities/tools.descartes.teastore.kieker.rabbitmq/ --push
	docker buildx build --platform ${DOCKER_PLATFORMS} -t "${registry}teastore-base" ../utilities/tools.descartes.teastore.dockerbase/ --push
	perl -i -pe's|.*FROM descartesresearch/|FROM '"${registry}"'|g' ../services/tools.descartes.teastore.*/Dockerfile
	docker buildx build --platform ${DOCKER_PLATFORMS} -t "${registry}teastore-registry" ../services/tools.descartes.teastore.registry/ --push
	docker buildx build --platform ${DOCKER_PLATFORMS} -t "${registry}teastore-persistence" ../services/tools.descartes.teastore.persistence/ --push
	docker buildx build --platform ${DOCKER_PLATFORMS} -t "${registry}teastore-image" ../services/tools.descartes.teastore.image/ --push
	docker buildx build --platform ${DOCKER_PLATFORMS} -t "${registry}teastore-webui" ../services/tools.descartes.teastore.webui/ --push
	docker buildx build --platform ${DOCKER_PLATFORMS} -t "${registry}teastore-auth" ../services/tools.descartes.teastore.auth/ --push
	docker buildx build --platform ${DOCKER_PLATFORMS} -t "${registry}teastore-recommender" ../services/tools.descartes.teastore.recommender/ --push
	perl -i -pe's|.*FROM '"${registry}"'|FROM descartesresearch/|g' ../services/tools.descartes.teastore.*/Dockerfile
	docker buildx rm mybuilder
else
	registry='descartesresearch/'
	docker buildx build -t "${registry}teastore-db" ../utilities/tools.descartes.teastore.database/ --load
	docker buildx build -t "${registry}teastore-kieker-rabbitmq" ../utilities/tools.descartes.teastore.kieker.rabbitmq/ --load
	docker buildx build -t "${registry}teastore-base" ../utilities/tools.descartes.teastore.dockerbase/ --load
	docker buildx build -t "${registry}teastore-registry" ../services/tools.descartes.teastore.registry/ --load
	docker buildx build -t "${registry}teastore-persistence" ../services/tools.descartes.teastore.persistence/ --load
	docker buildx build -t "${registry}teastore-image" ../services/tools.descartes.teastore.image/ --load
	docker buildx build -t "${registry}teastore-webui" ../services/tools.descartes.teastore.webui/ --load
	docker buildx build -t "${registry}teastore-auth" ../services/tools.descartes.teastore.auth/ --load
	docker buildx build -t "${registry}teastore-recommender" ../services/tools.descartes.teastore.recommender/ --load
fi

