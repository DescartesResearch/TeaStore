#!/bin/bash
# DANGER! MAKE SURE THIS FILE HAS UNIX-STYLE LINE-ENDINGS OR THE DOCKER CONTAINER WILL NOT START!
sed -i "s/%%SERVICE_PORT%%/${SERVICE_PORT}/g" /usr/local/tomcat/conf/context.xml
sed -i "s/%%HOST_NAME%%/${HOST_NAME}/g" /usr/local/tomcat/conf/context.xml
sed -i "s/%%REGISTRY_HOST%%/${REGISTRY_HOST}/g" /usr/local/tomcat/conf/context.xml
sed -i "s/%%REGISTRY_PORT%%/${REGISTRY_PORT}/g" /usr/local/tomcat/conf/context.xml
sed -i "s/%%DB_HOST%%/${DB_HOST}/g" /usr/local/tomcat/conf/context.xml
sed -i "s/%%DB_PORT%%/${DB_PORT}/g" /usr/local/tomcat/conf/context.xml
