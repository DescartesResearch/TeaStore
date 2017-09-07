FROM descartesresearch/petsupplystore-store:latest
MAINTAINER Chair of Software Engineering <se2-it@informatik.uni-wuerzburg.de>

# Set folder variables
ENV KIEKER_FOLDER /opt/kieker
ENV KIEKER_AGENT_FOLDER ${KIEKER_FOLDER}/agent
ENV KIEKER_CONFIG_FOLDER ${KIEKER_FOLDER}/config
ENV KIEKER_TMP_CONFIG_FOLDER ${KIEKER_FOLDER}/tmp-config
ENV KIEKER_LOGS_FOLDER ${KIEKER_FOLDER}/logs
ENV KIEKER_WEBAPPS_FOLDER ${KIEKER_FOLDER}/webapps
ENV KIEKER_TOMCAT_FOLDER /usr/local/tomcat
ENV KIEKER_TOMCAT_METAINF_FOLDER ${KIEKER_TOMCAT_FOLDER}/lib/META-INF
ENV KIEKER_TOMCAT_WEBAPPS_FOLDER ${KIEKER_TOMCAT_FOLDER}/webapps

# Set other variables
ENV KIEKER_MONITORING_PROPERTIES kieker.monitoring.properties
ENV KIEKER_AGENT_JAR agent.jar
# ENV KIEKER_AOP aop.xml
ENV KIEKER_AGENT_JAR_SRC "https://build.se.informatik.uni-kiel.de/jenkins/job/kieker-monitoring/job/kieker/job/master/lastSuccessfulBuild/artifact/build/libs/kieker-1.13-SNAPSHOT-aspectj.jar" 


COPY ${KIEKER_MONITORING_PROPERTIES} ${KIEKER_TMP_CONFIG_FOLDER}/${KIEKER_MONITORING_PROPERTIES}


RUN \
  mkdir -p ${KIEKER_AGENT_FOLDER} && \
  mkdir -p ${KIEKER_LOGS_FOLDER} && \
  mkdir -p ${KIEKER_TOMCAT_METAINF_FOLDER}
  
  
RUN \
  wget -q "${KIEKER_AGENT_JAR_SRC}" -O "${KIEKER_AGENT_FOLDER}/${KIEKER_AGENT_JAR}" && \
  ln -s ${KIEKER_WEBAPPS_FOLDER} ${KIEKER_TOMCAT_WEBAPPS_FOLDER} && \
  mkdir ${KIEKER_TOMCAT_WEBAPPS_FOLDER}/WEB-INF && \
  mkdir ${KIEKER_TOMCAT_WEBAPPS_FOLDER}/WEB-INF/lib && \
  cp ${KIEKER_AGENT_FOLDER}/${KIEKER_AGENT_JAR} ${KIEKER_TOMCAT_FOLDER}/lib/${KIEKER_AGENT_JAR} && \
  sed -i '250i\'"export KIEKER_JAVA_OPTS=\" \
    -javaagent:${KIEKER_AGENT_FOLDER}/${KIEKER_AGENT_JAR} \
    -Dkieker.monitoring.configuration=${KIEKER_CONFIG_FOLDER}/${KIEKER_MONITORING_PROPERTIES} \
    -Dkieker.monitoring.writer.filesystem.AsciiFileWriter.customStoragePath=${KIEKER_LOGS_FOLDER} \
    -Daj.weaving.verbose=true \
    -Dkieker.monitoring.skipDefaultAOPConfiguration=true \
    \"" /usr/local/tomcat/bin/catalina.sh && \
  sed -i '251i\'"export JAVA_OPTS=\"\${KIEKER_JAVA_OPTS} \${JAVA_OPTS}\"" /usr/local/tomcat/bin/catalina.sh
  
  
CMD \
  cp -nr ${KIEKER_TMP_CONFIG_FOLDER}/* ${KIEKER_CONFIG_FOLDER}/ && \
  rm ${KIEKER_TMP_CONFIG_FOLDER}/ -r && \
  /usr/local/tomcat/bin/start.sh &&  /usr/local/tomcat/bin/catalina.sh run

VOLUME ["/opt/kieker"]