FROM descartesresearch/teastore-base:latest
MAINTAINER Chair of Software Engineering <se2-it@informatik.uni-wuerzburg.de>

ARG VERSION=latest

COPY target/*.war /usr/local/tomcat/webapps/
COPY target/tools.descartes.teastore.recommender/WEB-INF/lib/kieker.probes-${VERSION}.jar /usr/local/tomcat/lib/