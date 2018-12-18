FROM tomcat:8.5-jre8
MAINTAINER Chair of Software Engineering <se2-it@informatik.uni-wuerzburg.de>

# Delete all example webapps from base container
RUN rm -r /usr/local/tomcat/webapps/examples

# Copy config, database connector library and script for environment variables into container
COPY baseContext.xml /usr/local/tomcat/conf/context.xml
COPY mysql-connector-java-5.1.44-bin.jar /usr/local/tomcat/lib/mysql-connector-java-5.1.44-bin.jar
COPY target/jars/dockermemoryconfigurator* /usr/local/tomcat/bin/dockermemoryconfigurator.jar
RUN rm /usr/local/tomcat/conf/server.xml
COPY server.xml /usr/local/tomcat/conf/

# Copy script to replace placeholders in context.xml with the environment variables
COPY start.sh /usr/local/tomcat/bin/start.sh
RUN chmod +x /usr/local/tomcat/bin/start.sh

# Service configuration. SERVICE_PORT is the host port, mapped to the exposed docker port. HOST_NAME
# is the IP address or hostname of the host running the docker container
ENV SERVICE_PORT 8080
ENV HOST_NAME unset
ENV USE_POD_IP false
ENV REGISTRY_HOST 127.0.0.1
ENV REGISTRY_PORT 8080
ENV DB_HOST 127.0.0.1
ENV DB_PORT 3306
ENV RECOMMENDER_RETRAIN_LOOP_TIME 0
ENV RECOMMENDER_ALGORITHM SlopeOne
ENV PROXY_NAME unset
ENV PROXY_PORT unset
ENV TOMCAT_HEAP_MEM_PERCENTAGE 50

# Setting secure random number generation to urandom. This increases the startup speed.
# Environment variable no longer needed. Replaced by sed in start.sh
# ENV JAVA_OPTS $JAVA_OPTS -Dsecurerandom.source=file:/dev/urandom

COPY kieker.monitoring.properties 							/kieker/config/kieker.monitoring.properties
COPY aop.xml 												/usr/local/tomcat/lib/aop.xml
COPY kieker-1.14-SNAPSHOT-aspectj.jar 						/kieker/agent/agent.jar

ENV JAVA_OPTS="${JAVA_OPTS} -javaagent:/kieker/agent/agent.jar"
ENV JAVA_OPTS="${JAVA_OPTS} -Dkieker.monitoring.configuration=/kieker/config/kieker.monitoring.properties"
ENV JAVA_OPTS="${JAVA_OPTS} -Daj.weaving.verbose=true"
ENV JAVA_OPTS="${JAVA_OPTS} -Dorg.aspectj.weaver.loadtime.configuration=aop.xml"
ENV JAVA_OPTS="${JAVA_OPTS} -Dkieker.monitoring.skipDefaultAOPConfiguration=true"
ENV JAVA_OPTS="${JAVA_OPTS} -Daj.weaving.loadersToSkip=java.net.URLClassLoader,sun.misc.Launcher\$$ AppClassLoader,sun.misc.Launcher\$$ ExtClassLoader"

EXPOSE 8080

CMD java -jar /usr/local/tomcat/bin/dockermemoryconfigurator.jar ${TOMCAT_HEAP_MEM_PERCENTAGE}; /usr/local/tomcat/bin/start.sh && /usr/local/tomcat/bin/catalina.sh run