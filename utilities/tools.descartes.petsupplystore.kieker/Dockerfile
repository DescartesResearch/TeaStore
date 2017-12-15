FROM descartesresearch/petsupplystore-base:latest
MAINTAINER Chair of Software Engineering <se2-it@informatik.uni-wuerzburg.de>

EXPOSE 8080

RUN mkdir /kieker
RUN mkdir /kieker/config
RUN mkdir /kieker/agent
RUN mkdir /kieker/logs

COPY kieker.monitoring.properties 									/kieker/config/kieker.monitoring.properties
COPY src/main/webapp/WEB-INF/lib/kieker-1.14-SNAPSHOT-aspectj.jar 	/kieker/agent/agent.jar

COPY target/*.war 													/usr/local/tomcat/webapps/

ENV JAVA_OPTS="${JAVA_OPTS} -javaagent:/kieker/agent/agent.jar"
ENV JAVA_OPTS="${JAVA_OPTS} -Dkieker.monitoring.configuration=/kieker/config/kieker.monitoring.properties"
ENV JAVA_OPTS="${JAVA_OPTS} -Daj.weaving.verbose=true"
ENV JAVA_OPTS="${JAVA_OPTS} -Dorg.aspectj.weaver.loadtime.configuration=/aop.xml"
ENV JAVA_OPTS="${JAVA_OPTS} -Dkieker.monitoring.skipDefaultAOPConfiguration=true"

CMD java -jar /usr/local/tomcat/bin/dockermemoryconfigurator.jar ${TOMCAT_HEAP_MEM_PERCENTAGE}; /usr/local/tomcat/bin/start.sh && /usr/local/tomcat/bin/catalina.sh run