#!/bin/bash
# DANGER! MAKE SURE THIS FILE HAS UNIX-STYLE LINE-ENDINGS OR THE DOCKER CONTAINER WILL NOT START!
sed -i "s/kieker.monitoring.writer.amqp.AmqpWriter.uri=amqp:\/\/admin:nimda@RABBITMQ_PORT_PLACEHOLDER/kieker.monitoring.writer.amqp.AmqpWriter.uri=amqp:\/\/admin:nimda@${RABBITMQ_HOST}/g" /kieker/config/kieker.monitoring.properties