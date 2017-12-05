FROM descartesresearch/petsupplystore-base:latest
MAINTAINER Chair of Software Engineering <se2-it@informatik.uni-wuerzburg.de>

EXPOSE 8080

ENV CATALINA_BASE="/usr/local/tomcat"

RUN mkdir $CATALINA_BASE/kieker
RUN mkdir $CATALINA_BASE/kieker/config
RUN mkdir $CATALINA_BASE/kieker/agent
RUN mkdir $CATALINA_BASE/kieker/logs

COPY kieker.monitoring.properties 									$CATALINA_BASE/kieker/config/kieker.monitoring.properties
COPY aop.xml					 									$CATALINA_BASE/aop.xml
COPY src/main/webapp/WEB-INF/lib/kieker-1.14-SNAPSHOT-aspectj.jar 	$CATALINA_BASE/kieker/agent/agent.jar

COPY target/*.war 													/usr/local/tomcat/webapps/

COPY setenv.sh	/usr/local/tomcat/bin/setenv.sh

ENV JAVA_OPTS="${JAVA_OPTS} -javaagent:$CATALINA_BASE/kieker/agent/agent.jar"
ENV JAVA_OPTS="${JAVA_OPTS} -Dkieker.monitoring.configuration=$CATALINA_BASE/kieker/config/kieker.monitoring.properties"
ENV JAVA_OPTS="${JAVA_OPTS} -Dkieker.monitoring.writer.filesystem.AsciiFileWriter.customStoragePath=$CATALINA_BASE/kieker/logs"
ENV JAVA_OPTS="${JAVA_OPTS} -Daj.weaving.verbose=true"
ENV JAVA_OPTS="${JAVA_OPTS} -Dorg.aspectj.weaver.loadtime.configuration=$CATALINA_BASE/aop.xml"
ENV JAVA_OPTS="${JAVA_OPTS} -Dkieker.monitoring.skipDefaultAOPConfiguration=true"

RUN /usr/local/tomcat/bin/start.sh
CMD /usr/local/tomcat/bin/catalina.sh run