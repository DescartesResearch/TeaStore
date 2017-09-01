#!/bin/sh
# Replace variables
sed -i "s/%%SERVICE_PORT%%/${SERVICE_PORT}/g" /usr/local/tomcat/conf/context.xml
sed -i "s/%%HOST_NAME%%/${HOST_NAME}/g" /usr/local/tomcat/conf/context.xml
sed -i "s/%%REGISTRY%%/${REGISTRY}/g" /usr/local/tomcat/conf/context.xml
sed -i "s/%%DB_HOST%%/${DB_HOST}/g" /usr/local/tomcat/conf/context.xml
sed -i "s/%%DB_PORT%%/${DB_PORT}/g" /usr/local/tomcat/conf/context.xml

# Run tomcat
/usr/local/tomcat/bin/catalina.sh run