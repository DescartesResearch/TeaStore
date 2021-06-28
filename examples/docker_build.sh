#!/bin/bash
push_flag='false'
registry='descartesresearch'
version='latest'

print_usage() {
  printf "Usage: docker_build.sh [-p] [-r REGISTRY_NAME] [-v VERSION]\n"
}

while getopts 'pr:' flag; do
  case "${flag}" in
    p) push_flag='true' ;;
    r) registry="${OPTARG}" ;;
    v) version="${OPTARG}" ;;
    *) print_usage
       exit 1 ;;
  esac
done

docker build --no-cache=true -t "$registry/teastore-base" ../utilities/tools.descartes.teastore.dockerbase/
perl -i -pe's|.*FROM descartesresearch/|FROM '"$registry"'/|g' ../services/tools.descartes.teastore.*/Dockerfile
docker build -t "$registry/teastore-registry" ../services/tools.descartes.teastore.registry/
docker build --build-arg VERSION=${version} -t "$registry/teastore-persistence" ../services/tools.descartes.teastore.persistence/
docker build --build-arg VERSION=${version} -t "$registry/teastore-image" ../services/tools.descartes.teastore.image/
docker build --build-arg VERSION=${version} -t "$registry/teastore-webui" ../services/tools.descartes.teastore.webui/
docker build --build-arg VERSION=${version} -t "$registry/teastore-auth" ../services/tools.descartes.teastore.auth/
docker build --build-arg VERSION=${version} -t "$registry/teastore-recommender" ../services/tools.descartes.teastore.recommender/
perl -i -pe's|.*FROM '"$registry"'/|FROM descartesresearch/|g' ../services/tools.descartes.teastore.*/Dockerfile

if [ "$push_flag" = 'true' ]; then
  docker push "$registry/teastore-base"
  docker push "$registry/teastore-registry"
  docker push "$registry/teastore-persistence"
  docker push "$registry/teastore-image"
  docker push "$registry/teastore-webui"
  docker push "$registry/teastore-auth"
  docker push "$registry/teastore-recommender"
fi