FROM tomcat:8.5.20-jre8
MAINTAINER Chair of Software Engineering <se2-it@informatik.uni-wuerzburg.de>

# Delete all example webapps from base container
RUN rm -r /usr/local/tomcat/webapps
RUN mkdir /usr/local/tomcat/webapps

# Copy config, database connector library and script for environment variables into container
COPY baseContext.xml /usr/local/tomcat/conf/context.xml
COPY mysql-connector-java-5.1.44-bin.jar /usr/local/tomcat/lib/mysql-connector-java-5.1.44-bin.jar

# Copy script to replace placeholders in context.xml with the environment variables
COPY start.sh /usr/local/tomcat/bin/start.sh
RUN chmod +x /usr/local/tomcat/bin/start.sh

# Service configuration. SERVICE_PORT is the host port, mapped to the exposed docker port. HOST_NAME
# is the IP address or hostname of the host running the docker container
ENV SERVICE_PORT 8080
ENV HOST_NAME 127.0.0.1
ENV REGISTRY_URL http://localhost:8080/tools.descartes.petsupplystore.registry/rest/services
ENV DB_HOST 127.0.0.1
ENV DB_PORT 3306

# Setting secure random number generation to urandom. This increases the startup speed.
ENV JAVA_OPTS $JAVA_OPTS -Dsecurerandom.source=file:/dev/urandom

EXPOSE 8080

ENTRYPOINT ["/usr/local/tomcat/bin/start.sh"]
#CMD["/usr/local/tomcat/bin/catalina.sh", "run"]