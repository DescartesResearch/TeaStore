#!/bin/bash
# DANGER! MAKE SURE THIS FILE HAS UNIX-STYLE LINE-ENDINGS OR THE DOCKER CONTAINER WILL NOT START!
sed -i "s/%%SERVICE_PORT%%/${SERVICE_PORT}/g" /usr/local/tomcat/conf/context.xml
if [ "$HOST_NAME" != "unset" ]
then
	sed -i "s/%%HOST_NAME%%/${HOST_NAME}/g" /usr/local/tomcat/conf/context.xml
else
	sed -i "/Environment name=\"hostName\"/d" /usr/local/tomcat/conf/context.xml
fi
sed -i "s/%%REGISTRY_HOST%%/${REGISTRY_HOST}/g" /usr/local/tomcat/conf/context.xml
sed -i "s/%%REGISTRY_PORT%%/${REGISTRY_PORT}/g" /usr/local/tomcat/conf/context.xml
sed -i "s/%%DB_HOST%%/${DB_HOST}/g" /usr/local/tomcat/conf/context.xml
sed -i "s/%%DB_PORT%%/${DB_PORT}/g" /usr/local/tomcat/conf/context.xml
if [ "$PROXY_NAME" != "unset" ] && [ "$PROXY_PORT" != "unset" ]
then
	sed -i "s/<Connector port=\"8080\" protocol=\"HTTP\/1.1\"/<Connector port=\"8080\" protocol=\"HTTP\/1.1\" proxyName=\"${PROXY_NAME}\" proxyPort=\"${PROXY_PORT}\"/g" /usr/local/tomcat/conf/server.xml
fi
sed -i 's/securerandom.source=file:\/dev\/random/securerandom.source=file:\/dev\/urandom/g' /usr/lib/jvm/java-8-openjdk-amd64/jre/lib/security/java.security
