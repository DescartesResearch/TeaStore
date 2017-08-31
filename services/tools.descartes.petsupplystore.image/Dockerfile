FROM tomcat:8.5.20-jre8
MAINTAINER Chair of Software Engineering <se2-it@informatik.uni-wuerzburg.de>

RUN rm -r /usr/local/tomcat/webapps
RUN mkdir /usr/local/tomcat/webapps

COPY target/tools.descartes.petsupplystore.image.war /usr/local/tomcat/webapps/tools.descartes.petsupplystore.image.war