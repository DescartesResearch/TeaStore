FROM rabbitmq:management
MAINTAINER Chair of Software Engineering <se2-it@informatik.uni-wuerzburg.de>

COPY apache-tomcat-8.5.24.zip /apache.zip
RUN apt-get update
RUN apt-get install unzip
RUN unzip /apache.zip
RUN chmod -R +x /apache-tomcat-8.5.24
RUN mkdir /usr/share/man/man1
RUN apt-get -y install default-jre-headless

EXPOSE 8080

CMD  rabbitmq-server --detached && rabbitmqadmin declare queue name=kieker durable=false && /apache-tomcat-8.5.24/bin/catalina.sh run