# Requirements

1. Java environment (jdk 8 or later)
2. Maven 
3. Local DNS server for HTTPS
  - e.g. dnsmasq https://zhimin-wen.medium.com/setup-local-dns-server-on-macbook-82ad22e76f2a


# Modifications to official Teastore

Configuration edits:
1. File ***web.xml*** for each service (i.e. webui, registry, recommender, persistence)
   Modify ServicePort and RegistryURL
   <env-entry>
		...
		<env-entry-value>8443</env-entry-value>
	</env-entry>
	<env-entry>
		...
		<env-entry-value>https://aida.bxmina.com:8443/tools.descartes.teastore.registry/rest/services/</env-entry-value>
	</env-entry>
   
2. file ***Server.xml*** dockerbase to include:
   
3. ***DockerFile*** of dockerbase to include:
   
4. ***start.sh*** of dockerbase to include:
   


Source code edits:

1. File ***RegistryClient*** 
2. File ***RESTClient*** 


# How to run and test with HTTPS:

docker-compose -f ./examples/docker/docker-compose_GOOD_AIDA.yaml up -d
