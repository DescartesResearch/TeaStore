#!/bin/bash
# DANGER! MAKE SURE THIS FILE HAS UNIX-STYLE LINE-ENDINGS OR THE DOCKER CONTAINER WILL NOT START!
sed -i "s/<Environment name=\"servicePort\" value=.*/<Environment name=\"servicePort\" value=\"${SERVICE_PORT}\"/g" /usr/local/tomcat/conf/context.xml
if [ "$HOST_NAME" != "unset" ]
then
	sed -i "s|<Environment name=\"hostName\" value=.*|<Environment name=\"hostName\" value=\"${HOST_NAME}\" type=\"java.lang.String\" override=\"false\"/>|g" /usr/local/tomcat/conf/context.xml
else
	sed -i "/Environment name=\"hostName\"/d" /usr/local/tomcat/conf/context.xml
fi
sed -i "s|<Environment name=\"useHostIP\" value=.*|<Environment name=\"useHostIP\" value=\"${USE_POD_IP}\"|g" /usr/local/tomcat/conf/context.xml
sed -i "s|<Environment name=\"registryURL\" value=.*|<Environment name=\"registryURL\" value=\"http://${REGISTRY_HOST}:${REGISTRY_PORT}/tools.descartes.teastore.registry/rest/services/\"|g" /usr/local/tomcat/conf/context.xml
sed -i "s/<Environment name=\"databaseHost\" value=.*/<Environment name=\"databaseHost\" value=\"${DB_HOST}\"/g" /usr/local/tomcat/conf/context.xml
sed -i "s/<Environment name=\"databasePort\" value=.*/<Environment name=\"databasePort\" value=\"${DB_PORT}\"/g" /usr/local/tomcat/conf/context.xml
sed -i "s/<Environment name=\"recommenderLoopTime\" value=.*/<Environment name=\"recommenderLoopTime\" value=\"${RECOMMENDER_RETRAIN_LOOP_TIME}\"/g" /usr/local/tomcat/conf/context.xml
sed -i "s/<Environment name=\"recommenderAlgorithm\" value=.*/<Environment name=\"recommenderAlgorithm\" value=\"${RECOMMENDER_ALGORITHM}\"/g" /usr/local/tomcat/conf/context.xml
if [ "$PROXY_NAME" != "unset" ] && [ "$PROXY_PORT" != "unset" ]
then
	sed -i "s/<Connector port=\"8080\" protocol=\"HTTP\/1.1\".*/<Connector port=\"8080\" protocol=\"HTTP\/1.1\" proxyName=\"${PROXY_NAME}\" proxyPort=\"${PROXY_PORT}\"/g" /usr/local/tomcat/conf/server.xml
fi
sed -i 's/securerandom.source=file:\/dev.*/securerandom.source=file:\/dev\/urandom/g' /usr/lib/jvm/java-8-openjdk-amd64/jre/lib/security/java.security
