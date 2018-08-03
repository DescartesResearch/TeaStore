FROM se-docker-registry.cloud.descartes.tools/teastore-base:latest
MAINTAINER Chair of Software Engineering <se2-it@informatik.uni-wuerzburg.de>

RUN  echo '<% response.sendRedirect("/tools.descartes.teastore.webui/"); %>' > /usr/local/tomcat/webapps/ROOT/index.jsp

COPY target/*.war /usr/local/tomcat/webapps/