FROM descartesresearch/teastore-base:latest
MAINTAINER Chair of Software Engineering <se2-it@informatik.uni-wuerzburg.de>

ARG VERSION=latest

COPY target/*.war /usr/local/tomcat/webapps/
COPY target/tools.descartes.teastore.webui/WEB-INF/lib/kieker.probes-${VERSION}.jar /usr/local/tomcat/lib/
RUN mkdir -p /usr/local/tomcat/webapps/ROOT

CMD java -jar /usr/local/tomcat/bin/dockermemoryconfigurator.jar ${TOMCAT_HEAP_MEM_PERCENTAGE}; /usr/local/tomcat/bin/start.sh && echo '<% response.sendRedirect("/tools.descartes.teastore.webui/"); %>' > /usr/local/tomcat/webapps/ROOT/index.jsp && /usr/local/tomcat/bin/catalina.sh run