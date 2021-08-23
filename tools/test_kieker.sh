#!/bin/bash

# Checks the teastore-rabbitmq for traces (works only in 'kieker mode'!)

HOST=${1} # usually 'localhost'
PORT=${2} # usually '8081', accesses the tomcat web interface of rabbitmq for the kieker logs
URL="http://${HOST}:${PORT}/logs/"

LOG_NAME=$(curl -s "${URL}" | grep -Eo -m 1 'kieker-.{,30}-UTC--/' | head -n 1)

if [ -z "${LOG_NAME}" ]
then
  echo 'No logs available!'
  exit 1
else
  if (( $(curl -s "${URL}${LOG_NAME}kieker.map" | grep -ci 'record') > 0 ));
  then
    DATA_NAME=$(curl -s "${URL}${LOG_NAME}" | grep -Eo -m 1 'kieker-.{,30}-UTC-001.dat' | head -n 1)
    DATA=$(curl -s "${URL}${LOG_NAME}${DATA_NAME}")
    if (( $(echo "${DATA}" | grep -cEi 'webui|auth|image|persistence|recommender|registry') > 0 ));
    then
      echo 'Kieker Test finished successfully!'
      exit 0
    else
      echo 'Dat data empty!'
      exit 1
    fi
  else
    echo 'Map data empty!'
    exit 1
  fi
fi
