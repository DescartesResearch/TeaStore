#!/bin/bash
if [ -z "$1" ]
  then
    echo "Provide a registry name!"
    exit -1
fi
docker build --no-cache=true -t $1/teastore-base utilities/tools.descartes.teastore.dockerbase/
perl -i -pe's|.*FROM descartesresearch/|FROM '"$1"'/|g' services/tools.descartes.teastore.*/Dockerfile
docker build -t $1/teastore-registry services/tools.descartes.teastore.registry/
docker build -t $1/teastore-persistence services/tools.descartes.teastore.persistence/
docker build -t $1/teastore-image services/tools.descartes.teastore.image/
docker build -t $1/teastore-webui services/tools.descartes.teastore.webui/
docker build -t $1/teastore-auth services/tools.descartes.teastore.auth/
docker build -t $1/teastore-recommender services/tools.descartes.teastore.recommender/
perl -i -pe's|.*FROM '"$1"'/|FROM descartesresearch/|g' services/tools.descartes.teastore.*/Dockerfile

docker push $1/teastore-base
docker push $1/teastore-registry
docker push $1/teastore-persistence
docker push $1/teastore-image
docker push $1/teastore-webui
docker push $1/teastore-auth
docker push $1/teastore-recommender
