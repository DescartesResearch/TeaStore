#!/bin/bash

# Tests the TeaStore in its default HTTP docker-compose configuration without kieker (docker-compose_default.yaml)

HOST=$1           # usually localhost
PROTO=$2          # supports 'http' and 'https'
WEBUI_PORT=$3     # 8080 for http, 8443 for https

# TODO: implement checks here
curl ${PROTO}://${HOST}:${WEBUI_PORT}/