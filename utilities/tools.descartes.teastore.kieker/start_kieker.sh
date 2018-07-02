#!/bin/bash
# DANGER! MAKE SURE THIS FILE HAS UNIX-STYLE LINE-ENDINGS OR THE DOCKER CONTAINER WILL NOT START!
sed -i "s/kieker.monitoring.writer.amqp.ChunkingAmqpWriter.uri=amqp:\/\/admin:nimda@RABBITMQ_PORT_PLACEHOLDER/kieker.monitoring.writer.amqp.ChunkingAmqpWriter.uri=amqp:\/\/admin:nimda@${RABBITMQ_HOST}/g" /kieker/config/kieker.monitoring.properties
if [ "$LOG_TO_FILE" == "true" ]
then
sed -i "s/kieker.monitoring.writer=kieker.monitoring.writer.collector.ChunkingCollector/kieker.monitoring.writer=kieker.monitoring.writer.filesystem.AsciiFileWriter/g" /kieker/config/kieker.monitoring.properties
fi
if [ "$LOG_PARAMETERS" == "true" ]
then
sed -i "s/kieker.monitoring.probe.aspectj.operationExecution.AbstractOperationExecutionAspect/tools.descartes.teastore.kieker.probes.AbstractOperationExecutionWithParameterAspect/g" /usr/local/tomcat/webapps/tools.descartes.teastore.webui/WEB-INF/classes/aop.xml
sed -i "s/kieker.monitoring.probe.aspectj.operationExecution.AbstractOperationExecutionAspect/tools.descartes.teastore.kieker.probes.AbstractOperationExecutionWithParameterAspect/g" /usr/local/tomcat/webapps/tools.descartes.teastore.auth/WEB-INF/classes/aop.xml
sed -i "s/kieker.monitoring.probe.aspectj.operationExecution.AbstractOperationExecutionAspect/tools.descartes.teastore.kieker.probes.AbstractOperationExecutionWithParameterAspect/g" /usr/local/tomcat/webapps/tools.descartes.teastore.recommender/WEB-INF/classes/aop.xml
sed -i "s/kieker.monitoring.probe.aspectj.operationExecution.AbstractOperationExecutionAspect/tools.descartes.teastore.kieker.probes.AbstractOperationExecutionWithParameterAspect/g" /usr/local/tomcat/webapps/tools.descartes.teastore.image/WEB-INF/classes/aop.xml
sed -i "s/kieker.monitoring.probe.aspectj.operationExecution.AbstractOperationExecutionAspect/tools.descartes.teastore.kieker.probes.AbstractOperationExecutionWithParameterAspect/g" /usr/local/tomcat/webapps/tools.descartes.teastore.persistence/WEB-INF/classes/aop.xml
fi