# The TeaStore

1. [Deploying the TeaStore](#1-deploying-the-teastore)
   1. [Run as Multiple Single Service Containers](#11-run-as-multiple-single-service-containers)
   2. [Run the TeaStore using Docker Compose](#12-run-the-teastore-using-docker-compose)
   3. [Run the TeaStore on a Kubernetes Cluster](#13-run-the-teastore-on-a-kubernetes-cluster)
   4. [Run the TeaStore with helm templates](#14-run-the-teastore-with-helm-templates)
2. [Using the TeaStore for Testing and Benchmarking](#2-using-the-teastore-for-testing-and-benchmarking)
   1. [Generating Load](#21-generating-load)
      1. [LIMBO HTTP Load Generator](#211-limbo-http-load-generator)
         1. [Deploying and starting the Load Generator](#2111-deploying-and-starting-the-load-generator)
         2. [Create/Download a Load Intensity Profile](#2112-createdownload-a-load-intensity-profile)
         3. [Create/Download a Request Definition Script](#2113-createdownload-a-request-definition-script)
         4. [Run the load generator](#2114-run-the-load-generator)
            1. [Simple Configuration](#21141-simple-configuration)
            2. [Measure the CPU utilization of containers over the Docker API](#21142-measure-the-cpu-utilization-of-containers-over-the-docker-api)
      2. [JMeter™](#212-jmeter)
         1. [Run JMeter™ with GUI](#2121-run-jmeter-with-gui)
         2. [Run JMeter™ with Command-Line](#2122-run-jmeter-with-command-line)
      3. [Locust](#213-locust)
   2. [Instrumenting the TeaStore](#22-instrumenting-the-teastore)
      1. [Docker containers with Kieker](#221-docker-containers-with-kieker)
         1. [AMQP Logging](#2211-amqp-logging)
         2. [AMQP Logging in Kubernetes](#2212-amqp-logging-in-kubernetes)
         3. [Local Logging](#2213-local-logging)
         4. [Parameter Logging](#2214-parameter-logging)
      2. [OpenTracing with Kubernetes and Istio](#222-opentracing-with-kubernetes-and-istio)
3. [Building and Customizing the TeaStore](#3-building-and-customizing-the-teastore)

The TeaStore is a micro-service reference and test application developed by the Descartes Research Group at the University of Würzburg. The TeaStore emulates a basic web store for automatically generated, fictitious teas, tea accessories and supplies. As it is primarily a test application, it features UI elements for database generation and service resetting in addition to the store itself.

The TeaStore is a distributed micro-service application featuring five distinct services plus a registry. Each service may be replicated without limit and deployed on separate devices as desired. Services communicate using REST and using the Netflix "[Ribbon](https://github.com/Netflix/ribbon)" client side load balancer. The five services are as follows:
* _tools.descartes.teastore.webui_: WebUI Service
* _tools.descartes.teastore.auth_: Authentication Service
* _tools.descartes.teastore.recommender_: Recommender Service
* _tools.descartes.teastore.persistence_: Persistence Provider Service
* _tools.descartes.teastore.image_: Image Provider Service

Services register at a separate simple registry, which is provided with the TeaStore. Any service registering with the registry is automatically called by all other services which require it.

The TeaStore is designed to be a reference / test application to be used in benchmarks and tests. Some of its envisioned use-cases are:
* Testing performance model extractors and predictors for distributed applications
* Testing micro-service and cloud management mechanisms, such as multi-tier auto-scalers
* Testing energy efficiency and power prediction and management mechanisms

Note that the TeaStore does not feature a front-end load balancer for the WebUI. If you want to use multiple WebUI instances, you must configure a front-end load balancer yourself or configure your load driver to use all available WebUI instances.

## 1. Deploying the TeaStore

We currently offer multiple options to deploy the TeaStore:
- Docker with manual setup (recommended)
- Docker Compose
- Kubernetes
- Helm Charts

Other outdated methods are mentioned in the GitHub Wiki.

### 1.1. Run as Multiple Single Service Containers

Running the TeaStore as single use containers is the recommended way for benchmarking, testing and modelling. The store consists of the registry image, five service images and a pre-configured database image running MariaDB. All images to run the containers are shown below. All containers except `teastore-db` support different environment variables that can be set on container start. Instead of running each service with a separate `docker run`, we also provide sample docker-compose files in the next section.

* _REGISTRY\_HOST_ : The host name or IP of the machine running the registry container.
* _REGISTRY\_PORT_ : The port of the machine running the registry container.
* _HOST\_NAME_ : The host name or IP of the machine that will be running the container. Uses the OS-provided hostname if unset.
    * Alternatively: _USE\_POD\_IP_ : Use the Container's (or Pod's) IP as hostname when set to `true` and ignore the _HOST\_NAME_ variable. Use this instead of _HOST\_NAME_ when deploying in Kubernetes.
* _SERVICE\_PORT_ : The port of the machine that will be running the container and that is published via `-p`. The service will register at the registry using this port.
* _DB\_HOST_ : The host name or IP of the machine running the database (only needed for persistence service).
* _DB\_PORT_ : The port the database is bound to (only needed for persistence servcie).
* _PROXY\_NAME_ : Name of the front-end load balancer proxy (should only be needed for WebUI and only if it uses a front-end load balancer).
* _PROXY\_PORT_ : Port of the front-end load balancer proxy (should only be needed for WebUI and only if it uses a front-end load balancer).
* _RECOMMENDER\_RETRAIN\_LOOP\_TIME_ : Time in ms for the Recommender to wait before retraining itself. (Optional parameter for Recommender, set as 0 (disabled) by default).
* _RECOMMENDER\_ALGORITHM_ : Recommendation algorithm, valid values: "Popularity", "SlopeOne", "PreprocessedSlopeOne", "OrderBased". (Optional parameter for Recommender, set as "SlopeOne" by default).


All TeaStore docker images with their respective environment variables:

* [`descartesresearch/teastore-registry`](https://hub.docker.com/r/descartesresearch/teastore-registry)
    * Mandatory: SERVICE\_PORT
    * Recommended: HOST\_NAME
* [`descartesresearch/teastore-webui`](https://hub.docker.com/r/descartesresearch/teastore-webui)
    * Mandatory: REGISTRY\_HOST, REGISTRY\_PORT, SERVICE\_PORT
    * Recommended: HOST\_NAME (or USE_POD_IP=true in Kubernetes)
    * Optional: PROXY\_NAME, PROXY\_PORT
* [`descartesresearch/teastore-auth`](https://hub.docker.com/r/descartesresearch/teastore-auth)
    * Mandatory: REGISTRY\_HOST, REGISTRY\_PORT, SERVICE\_PORT
    * Recommended: HOST\_NAME (or USE_POD_IP=true in Kubernetes)
* [`descartesresearch/teastore-persistence`](https://hub.docker.com/r/descartesresearch/teastore-persistence)
    * Mandatory: REGISTRY\_HOST, REGISTRY\_PORT, SERVICE\_PORT, DB\_HOST, DB\_PORT
    * Recommended: HOST\_NAME (or USE_POD_IP=true in Kubernetes)
* [`descartesresearch/teastore-recommender`](https://hub.docker.com/r/descartesresearch/teastore-recommender)
    * Mandatory: REGISTRY\_HOST, REGISTRY\_PORT, SERVICE\_PORT
    * Recommended: HOST\_NAME (or USE_POD_IP=true in Kubernetes)
    * Optional: RECOMMENDER\_RETRAIN\_LOOP\_TIME, RECOMMENDER\_ALGORITHM
* [`descartesresearch/teastore-image`](https://hub.docker.com/r/descartesresearch/teastore-image)
    * Mandatory: REGISTRY\_HOST, REGISTRY\_PORT, SERVICE\_PORT
    * Recommended: HOST\_NAME (or USE_POD_IP=true in Kubernetes)
* [`descartesresearch/teastore-db`](https://hub.docker.com/r/descartesresearch/teastore-db)
    * _none_

The TeaStore was built with robustness in mind.
Despite dependencies among services (e.g. all services need the Registry service) the services can be started independently of each other since all services try to connect to another required service as often as needed.
The host name (or IP) as well as the used port of the services must be known.

The registry and services can be started with the following commands. Remember to replace the example values with the correct values for your setup. The host name and port refer to the system running the docker service. Make sure that _SERVICE\_PORT_ and the port mapping to the docker container (`-p 10000:8080`) are identical.
In the following example we assume the registry is running on server 10.1.2.3 on port 10000, the other services running on 10.1.2.30 and the database at 10.1.2.20:3306. The values must be changed accordingly.

1. Registry
   `docker run -e "HOST_NAME=10.1.2.3" -e "SERVICE_PORT=10000" -p 10000:8080 -d descartesresearch/teastore-registry`
2. Database: The database container is needed for the persistence service and can be started with the following command.
   The image comes preconfigured and no configuration is necessary. This step can be skipped if you use your own MySQL or MariaDB. The host name or IP of your database server/container must be known to start the persistence service correctly.
   `docker run -p 3306:3306 -d descartesresearch/teastore-db`
3. Persistence
   `docker run -e "REGISTRY_HOST=10.1.2.3" -e "REGISTRY_PORT=10000" -e "HOST_NAME=10.1.2.30" -e "SERVICE_PORT=1111" -e "DB_HOST=10.1.2.20" -e "DB_PORT=3306" -p 1111:8080 -d descartesresearch/teastore-persistence`
4. Store
   `docker run -e "REGISTRY_HOST=10.1.2.3" -e "REGISTRY_PORT=10000" -e "HOST_NAME=10.1.2.30" -e "SERVICE_PORT=2222" -p 2222:8080 -d descartesresearch/teastore-auth`
5. Recommender
   `docker run -e "REGISTRY_HOST=10.1.2.3" -e "REGISTRY_PORT=10000" -e "HOST_NAME=10.1.2.30" -e "SERVICE_PORT=3333" -p 3333:8080 -d descartesresearch/teastore-recommender`
6. Image Provider
   `docker run -e "REGISTRY_HOST=10.1.2.3" -e "REGISTRY_PORT=10000" -e "HOST_NAME=10.1.2.30" -e "SERVICE_PORT=4444" -p 4444:8080 -d descartesresearch/teastore-image`
7. WebUI
   `docker run -e "REGISTRY_HOST=10.1.2.3" -e "REGISTRY_PORT=10000" -e "HOST_NAME=10.1.2.30" -e "SERVICE_PORT=8080" -p 8080:8080 -d descartesresearch/teastore-webui`

Note that this example does not configure the _PROXY\_NAME_ and _PROXY\_PORT_ variables for the WebUI, as it assumes that no front-end load balancer is used. If one is used, both of these variables may be configured for the WebUI.

### 1.2. Run the TeaStore using Docker Compose

The multiple single container setup is suitable for to be used with Docker Compose as well.
One advantage of using docker-compose is the easy setup of internal networks. This allows the services to communicate with each other using hostnames defined as the service name in the docker-compose file.

We provide two sample docker-compose files, located under the `examples/docker/` after cloning the repository:
1. To deploy the TeaStore with Kieker monitoring enabled use:
   `docker-compose -f ./examples/docker/docker-compose_kieker.yaml up -d`
2. To deploy the TeaStore without Kieker use:
   `docker-compose -f ./examples/docker/docker-compose_default.yaml up -d`

Both variants by default expose the TeaStore WebUI on port `8080` allowing access to the WebUI under `localhost:8080/tools.descartes.teastore.webui/`.

Of course, the same environment variables described in the previous section can be used in the docker-compose file as well.

### 1.3. Run the TeaStore on a Kubernetes Cluster

The TeaStore Docker containers can be used in a Kubernetes Setup. In general, we see two ways of configuring TeaStore on Kubernetes:

1. Configure Kubernetes to deploy the TeaStore pods, but have pods resolve each other and communicate directly using Ribbon (the client side load balancer provided inside each TeaStore service instance).
2. Configure TeaStore to use Kubernetes _ClusterIP_ for addressing services. This way, all communication is routed through Kubernetes.

We see the following benefits and drawbacks of both methods: (1) Using Ribbon in Kubernetes emulates the communication and performance of a non-Kubernetes setup, enhancing comparability. Therefore, we recommend this option. (2) Using _ClusterIP_ enables use and instrumentation of internal Kubernetes load balancing, which is useful for tests and research focussing on these aspects. It is noteworthy that the second option "tricks" the TeaStore registry into thinking that only a single instance of each service is present. The TeaStore's service status report page will not provide correct results in this case.

We provide yamls to quickly deploy setups for both variants:

1. To deploy TeaStore using the client side load Balancer Ribbon use:  
   `kubectl create -f https://raw.githubusercontent.com/DescartesResearch/TeaStore/master/examples/kubernetes/teastore-ribbon.yaml`
2. To deploy TeaStore using the Kubernetes _ClusterIP_ feature use:  
   `kubectl create -f https://raw.githubusercontent.com/DescartesResearch/TeaStore/master/examples/kubernetes/teastore-clusterip.yaml`

Both variants expose the TeaStore WebUI using _NodeIP_ on Port `30080`, you can access the UI using `http://K8S_NODE_IP:30080/tools.descartes.teastore.webui/`. You can change the way the TeaStore is exposed starting in line 188 in `teastore-ribbon.yaml` and line 241 in `teastore-clusterip.yaml`.
Note that both variants label all deployments, pods and services with the `app=teastore` label, making it easy to shutdown the entire teastore using `kubectl delete pods,deployments,services -l app=teastore`. Also note that our yamls do not define resource requests. You will have to add these if you want to enable utilization based autoscaling.

We also provide yamls to run an instrumented variant of the TeaStore in Kubernetes using [Kieker](http://kieker-monitoring.net).

### 1.4. Run the TeaStore with helm templates

1. Install minikube or any kind of kubernetes cluster and configure it with `kubectl`.
2. Install `helm`.
3. Run `helm install teastore examples/helm/` in the repository root.
4. Run `kubectl port-forward teastore-webui-0 8080:8080` to access the webui on `localhost:8080`.

All the configuration is done in the `values.yaml`. 
The only special feature of this helm chart is the `clientside_loadbalancer` variable which configures whether a StatefulSet or a Deployment with Kubernetes LoadBalancing is deployed. 
Of course you can also use regular helm command line overwrites instead of the yaml file(`--set clientside_loadbalancer=true`).
If you need custom service URLs you can specify them in the format servicename.url (e. g. `db.url=mydb.servicemesh`).

## 2. Using the TeaStore for Testing and Benchmarking

To use the TeaStore for benchmarking and/or testing, one usually needs at least a load generator. More commonly, a testing harness with integrated load generation is employed. In addition, some instrumentation may be required or useful.

### 2.1. Generating Load

Generate load by sending HTTP requests either to the WebUI service instances or to a self-configured front-end load balancer. You can send requests using your browser. For more controlled testing and for testing at higher loads, you may want to use a load generator. Any HTTP load generator capable of sending POST and GET requests and supporting Cookies should work with the Tea Store.

We recommend using one of our tested load generators:

1. LIMBO HTTP Load Generator: High-performance load generator for dynamically varying loads. Scripts requests using LUA scripts and supports power measurements.
2. JMeter™: Established web application testing tool with test definition UI and many available plug-ins.
3. Locust: Scalable user load testing tool written in Python

#### 2.1.1. LIMBO HTTP Load Generator

The [LIMBO HTTP Load Generator](https://github.com/joakimkistowski/HTTP-Load-Generator) (download the binary [here](https://gitlab2.informatik.uni-wuerzburg.de/descartes/httploadgenerator/raw/master/httploadgenerator.jar)) is a load generator designed to generate HTTP loads with varying load intensities. It uses load intensity specifications as specified by [LIMBO](http://descartes.tools/limbo) to generate loads that vary in intensity (number of requests per second) over time. The load generator logs application level data and supports connecting to external power measurement daemons. It specifies the HTTP requests themselves using LUA scripts, which are read at run-time. You can get an overview of the HTTP Load Generator by reading its [online documentation](https://github.com/joakimkistowski/HTTP-Load-Generator/blob/master/README.md).

The HTTP Load Generator must be deployed on at least two machines to run. It uses at least one load generator machine from which load is generated, requests are sent, and responses are received. As an additional machine, it must be deployed on a director machine (this is usually your PC) from which the test is controlled. Config files are read from the director machine and result files are written to this machine as well.

To run the TeaStore with the LIMBO HTTP Load Generator, you must do the following (assuming the TeaStore is already up and running):

1. Deploy and start the load generator
2. Create/download a load intensity profile
3. Create/download a request definition script
4. Run the load generator

##### 2.1.1.1. Deploying and starting the Load Generator

First choose a load generator machine. Deploy the HTTP Load Generator Jar on the load generator machine and start it in load generator mode with the _loadgenerator_ command:

    $ java -jar httploadgenerator.jar loadgenerator

##### 2.1.1.2. Create/Download a Load Intensity Profile

Next, download or create a load intensity profile for your test. To create your own, we recommend using the [LIMBO tool](http://descartes.tools/limbo). You can also simply write your own CSV file. The format and use of LIMBO for load profile creation is documented [here](https://github.com/joakimkistowski/HTTP-Load-Generator#31-creating-a-custom-load-intensity-arrival-rate-profile).

We provide these example load intensity profiles:

* [Low intensity stress test profile](https://github.com/DescartesResearch/teastore/blob/master/examples/httploadgenerator/increasingLowIntensity.csv): A profile with a linearly increasing load intensity for 2 minutes. Intensity increases to up to 100 requests per second. Use this profile if you are getting started.
*  [Medium intensity stress test profile](https://github.com/DescartesResearch/teastore/blob/master/examples/httploadgenerator/increasingMedIntensity.csv): A profile with a linearly increasing load intensity for 2 minutes. Intensity increases to up to 1000 requests per second.
* [High intensity stress test profile](https://github.com/DescartesResearch/teastore/blob/master/examples/httploadgenerator/increasingHighIntensity.csv): A profile with a linearly increasing load intensity for 2 minutes. Intensity increases to up to 2000 requests per second.

Place your load intensity profile on the director machine. For simplicity, the following instructions will assume that you have placed it in the same directory as the _httploadgenerator.jar_.

##### 2.1.1.3. Create/Download a Request Definition Script

The request definition script defines which requests to send. It can create context-sensitive requests based on the responses and is defined in the LUA scripting language. The library functions to be used in the script are documented [here](https://github.com/joakimkistowski/HTTP-Load-Generator#32-scripting-the-requests-themselves).

To start, download one of our TeaStore request profiles and place it on the director machine. For simplicity, place it in the same directory as the _httploadgenerator.jar_.

We provide these two request scripts:

* [RECOMMENDED: Browse Profile](https://github.com/DescartesResearch/teastore/blob/master/examples/httploadgenerator/teastore_browse.lua): A profile that emulates users browsing the store, adding items to their shopping carts and then discarding the carts. Users place no orders and the database remains unchanged. Use this one as your go-to profile.
* [Buy Profile](https://github.com/DescartesResearch/TeaStore/blob/master/examples/httploadgenerator/teastore_buy.lua): A profile that emulates users browsing the store and purchasing items. This profile changes the database over time and may thus lead to less stable behavior.

##### 2.1.1.4. Run the load generator

We provide two options to run the load generator: The simple (original) configuration and one that allows measuring the CPU utilization of docker containers over the docker API.

###### 2.1.1.4.1 Simple Configuration

To run the load generator, place the request definition script, load intensity profile, and _httploadgenerator.jar_ on your director machine and start the _httploadgenerator.jar_ in load generation mode (using the _loadgenerator_ command) on the load generator machine.

You can run the test from your director machine by starting the _httploadgenerator.jar_ in director mode (using the _-director_ command). A typical start command looks like this:

    $ java -jar httploadgenerator.jar director -s 10.1.1.1 -a ./MYLOADINTENSITYPROFILE.csv -l ./MYREQUESTPROFILE.lua -o MYOUTPUTFILENAME.csv -t 256

The example uses the following command-line switches (feel free to use the _director -h_ switch for help):

* _director_ : starts the **d**irector mode (required).
* _-s_ : specifies the addres**s** of the load generator machine. Use comma separated addresses if you use multiple load generator machines.
* _-a_ : specifies the **a**rrival rate file (load intensity profile).
* _-l_ : specifies the **l**ua user profile script.
* _-o_ : specifies the name of the **o**utput result csv file.
* _-t_ : specifies the number of load generation **t**hreads.

###### 2.1.1.4.2 Measure the CPU utilization of containers over the Docker API

For this functionality, a [modified binary](https://github.com/DescartesResearch/TeaStore/blob/master/examples/httploadgenerator/httploadgenerator.jar) is needed. This binary can be used just like the version from the simple configuration, but two additional parameters have to be specified:

- The `-c` option in `director` mode must be set to `docker`.
- In `-p` must be comma-separated values `<IP>:<PORT>:<CONTAINER_ID>,<IP>:<PORT>:<CONTAINER_ID>`. `<IP>` and `<PORT>` specify the IP and port of the server providing access to the docker API. The `<CONTAINER_ID>` is the ID of the desired container in `docker ps` to get the cpu utilization of.

Additionally, the Docker API must be available over TCP on the server. Therefore, use `socat` to create a unix socket "proxy":

```shell
# make the docker API available over TCP on port 12345. may require 'sudo' to work. 
# USE WITH CAUTION, because this exposes docker functionality to the entire network!
socat TCP-LISTEN:12345,reuseaddr,fork UNIX-CLIENT:/var/run/docker.sock
```

The utilization data for each container is appended to the output `.csv` file of the load generator director.

#### 2.1.2. JMeter™

The Apache JMeter™ (download the binary [here](https://jmeter.apache.org/download_jmeter.cgi)) application is open source software, a 100% pure Java application designed to load test functional behavior and measure performance.

For stressing the TeaStore we uploaded two scipts: (i) [Browse Profile](https://github.com/DescartesResearch/TeaStore/blob/master/examples/jmeter/teastore_browse.jmx) for the usage of the GUI and (ii) [Browse Profile](https://github.com/DescartesResearch/TeaStore/blob/master/examples/jmeter/teastore_browse_nogui.jmx) for the usage of a command-line.

##### 2.1.2.1. Run JMeter™ with GUI
First, download the [script](https://github.com/DescartesResearch/TeaStore/blob/master/examples/jmeter/teastore_browse.jmx). Then, open JMeter™ and open teastore_browse.jmx. Afterwards, click on the testplan called TeaStore. Here, 4 variables need to be set:

* _hostname_ : specifies the address of the WebUI or the front load balancer, which handles multiple WebUIs (required).
* _port_ : specifies the port of the TeaStore (required).
* _num\_user_ : specifies the number of users (required)
* _ramp\_up_ : specifies the time until all users are active (required).

After the setting of the variables, click the _Start_-button. To stop the run, click the _Stop_-button. The default option is that the thread group loops forever. However, a number of loops can be set by _Loop Count_. If a finit number of loops is set, the scipt terminates automatically after the last loop.

The testplan offers a summary report, in which statistics of each request is reported, and a result tree, in which each request is reported.

##### 2.1.2.2. Run JMeter™ with Command-Line
We support also using JMeter™ with a command line. First, download the [script](https://github.com/DescartesResearch/TeaStore/blob/master/examples/jmeter/teastore_browse_nogui.jmx). Then, type the following command, which contains examplary values, into the command-line:

    $ java -jar ApacheJMeter.jar -t teastore_browse_nogui.jmx -Jhostname 10.1.1.1 -Jport 8080 -JnumUser 10 -JrampUp 1 -l mylogfile.log -n

For the command-line, the following switches are used:

* _-t_ : specifies the jmx user profile scipt (required).
* _-Jhostname_ : specifies the address of the WebUI or the front load balancer, which handles multiple WebUI (required).
* _-Jport_ : specifies the port (required).
* _-JnumUser_ : specifies the number of concurrent users (required).
* _-JrampUp_ : specifies the time until all users are active (required).
* _-l_ : specifies the name of outputfile.
* _-n_ : starts the scipt without the gui (required).

If the switch _-l_ is set, the results of the run are stored in the specified file. The default option is the script loops forever. However, a number of loops can be set. Thus, open the script with the JMeter™ GUI, set _Loop Count_, and save. If a finit number of loops is set, the scipt terminates automatically after the last loop.

#### 2.1.3. Locust

[Locust](https://github.com/locustio/locust) is an easy to use, scriptable and scalable performance testing tool written in Python.

To use it with the Teastore, it has to be installed via Python pip:

`pip install locust`

Go to the directory `example/locust` and start the GUI by executing the command `locust` in the shell. After that the GUI is available under http://localhost:8089.
There you need to configure the host url of the Teastore webui, the number of simulated users and their spawn rate.

Further instructions and information on e.g. customization and scripting are available in the [documentation](https://docs.locust.io/en/stable/index.html).

### 2.2. Instrumenting the TeaStore

The following section discusses the options to take measurements from the TeaStore using Kieker in Docker/Kubernetes or OpenTracing.

#### 2.2.1. Docker containers with Kieker

The docker containers we provide for all services are already instrumented using [Kieker](http://kieker-monitoring.net/).
They can either log to an AMQP server or locally to a File. Both options are discussed in the following.

##### 2.2.1.1. AMQP Logging

Collecting the Kieker logs via AMQP is currently the preferred setup. To set up the TeaStore with log collection via AMQP, the regular setup described here is extended by a "RABBITMQ_HOST=X.X.X.X" flag and a preconfigred RabbitMQ container [descartesresearch/teastore-kieker-rabbitmq](https://hub.docker.com/r/descartesresearch/teastore-kieker-rabbitmq/). The RABBITMQ_HOST parameter is only required for services that support Kieker monitoring, i.e. WebUI, ImageProvider, Auth, Recommender, and Persistence. To set up an instrumented TeaStore version, first you start a standard registry and database:

`docker run -e "HOST_NAME=10.1.2.3" -e "SERVICE_PORT=10000" -p 10000:8080 -d descartesresearch/teastore-registry`

`docker run -p 3306:3306 -d descartesresearch/teastore-db`

Next, start the preconfigured AMQP server container and expose the required ports:

`docker run -p 8080:15672 -d --expose 8080 -p 5672:5672 -p 8081:8080 descartesresearch/teastore-kieker-rabbitmq`

On port 8080 the RabbitMQ Dashboard can be accessed with the username `admin` and the password `nimda`. Here, all connections to the server can be monitored. The monitoring traces of all connected services are saved into a single file and can be accessed at `/logs/` using port 8081.

Next, start all other services with their respective `docker run` command. We assume that the registry is running on server 10.1.2.3 on port 10000, the TeaStore services are running on 10.1.2.30, the database at 10.1.2.20:3306 and the AMQP container at 10.1.2.21 (ports are fixed for the AMQP container). The values must be changed accordingly for your setup.

1. Persistence
   `docker run -e "REGISTRY_HOST=10.1.2.3" -e "REGISTRY_PORT=10000" -e "HOST_NAME=10.1.2.30" -e "SERVICE_PORT=1111" -e "DB_HOST=10.1.2.20" -e "DB_PORT=3306" -e "RABBITMQ_HOST=10.1.2.21" -p 1111:8080 -d descartesresearch/teastore-persistence`
2. Authentication
   `docker run -e "REGISTRY_HOST=10.1.2.3" -e "REGISTRY_PORT=10000" -e "HOST_NAME=10.1.2.30" -e "SERVICE_PORT=2222" -e "RABBITMQ_HOST=10.1.2.21" -p 2222:8080 -d descartesresearch/teastore-auth`
3. Recommender
   `docker run -e "REGISTRY_HOST=10.1.2.3" -e "REGISTRY_PORT=10000" -e "HOST_NAME=10.1.2.30" -e "SERVICE_PORT=3333" -e "RABBITMQ_HOST=10.1.2.21" -p 3333:8080 -d descartesresearch/teastore-recommender`
4. Image Provider
   `docker run -e "REGISTRY_HOST=10.1.2.3" -e "REGISTRY_PORT=10000" -e "HOST_NAME=10.1.2.30" -e "SERVICE_PORT=4444" -e "RABBITMQ_HOST=10.1.2.21" -p 4444:8080 -d descartesresearch/teastore-image`
5. WebUI
   `docker run -e "REGISTRY_HOST=10.1.2.3" -e "REGISTRY_PORT=10000" -e "HOST_NAME=10.1.2.30" -e "SERVICE_PORT=8080" -e "RABBITMQ_HOST=10.1.2.21" -p 8080:8080 -d descartesresearch/teastore-webui`

The Kieker traces can now be downloaded at [http://10.1.2.21:8081/logs/](http://10.1.2.21:8081/logs/) as a single file containing the monitoring traces for all service instances. To run analysis on the generated Kieker traces, follow the documentation [here](http://eprints.uni-kiel.de/16537/67/kieker-1.12-userguide.pdf).

##### 2.2.1.2. AMQP Logging in Kubernetes

The TeaStore can be deployed with Kieker Monitoring and AMQP Logging in Kubernetes. We provide two Kubernetes yamls to do so. First, the AMQP server must be deployed. It must be online and ready to receive messages when the instrumented TeaStore services are started.

1. To deploy the AMQP server run:  
   `$ kubectl create -f https://raw.githubusercontent.com/DescartesResearch/TeaStore/development/examples/kubernetes/teastore-rabbitmq.yaml`

2. Next, deploy the instrumented variant of the TeaStore:  
   `$ kubectl create -f https://raw.githubusercontent.com/DescartesResearch/TeaStore/development/examples/kubernetes/teastore-ribbon-kieker.yaml`

As with the non-instrumented setup, the TeaStore WebUI is exposed using *NodeIP* on Port `30080`, you can access the UI using `http://K8S_NODE_IP:30080/tools.descartes.teastore.webui/`. You can download the logs from `http://K8S_NODE_IP:30081/logs/`, also exposed using *NodeIP*.
Again, all deployments, pods and services of the instrumentation infrastructure and the TeaStore itself are labeled with the app=teastore label, making it easy to shutdown everything using `kubectl delete pods,deployments,services -l app=teastore`.

You can watch a short demonstration of the TeaStore running in Kubernetes AMQP Logging on **[YouTube](https://www.youtube.com/watch?v=6OcSNrErzGE&feature=youtu.be)**.

##### 2.2.1.3. Local Logging

In scenarios where no AMQP server is available, logging to a local file can be enabled. To set up service with local logging, the regular setup is extended by a "LOG_TO_FILE=true" flag. Additionally, we recommend mounting the log directory inside the container as a volume to make the logs accessible outside the container. This parameter is only required for services that support Kieker monitoring, i.e. WebUI, ImageProvider, Auth, Recommender, and Persistence. Therefore, to set up an instrumented TeaStore version, first you start a standard registry and database:

`docker run -e "HOST_NAME=10.1.2.3" -e "SERVICE_PORT=10000" -p 10000:8080 -d descartesresearch/teastore-registry`

`docker run -p 3306:3306 -d descartesresearch/teastore-db`

Next, start all other services with their respective `docker run` command. We assume that the registry is running on server 10.1.2.3 on port 10000, the TeaStore services are running on 10.1.2.30 and the database at 10.1.2.20:3306. The values must be changed accordingly for your setup.

1. Persistence
   `docker run -v /home/myUserName/myFolder:/kieker/logs/ -e "LOG_TO_FILE=true" -e "REGISTRY_HOST=10.1.2.3" -e "REGISTRY_PORT=10000" -e "HOST_NAME=10.1.2.30" -e "SERVICE_PORT=1111" -e "DB_HOST=10.1.2.20" -e "DB_PORT=3306" -p 1111:8080 -d descartesresearch/teastore-persistence`
2. Authentication
   `docker run -v /home/myUserName/myFolder:/kieker/logs/ -e "LOG_TO_FILE=true" -e "REGISTRY_HOST=10.1.2.3" -e "REGISTRY_PORT=10000" -e "HOST_NAME=10.1.2.30" -e "SERVICE_PORT=2222" -p 2222:8080 -d descartesresearch/teastore-auth`
3. Recommender
   `docker run -v /home/myUserName/myFolder:/kieker/logs/ -e "LOG_TO_FILE=true" -e "REGISTRY_HOST=10.1.2.3" -e "REGISTRY_PORT=10000" -e "HOST_NAME=10.1.2.30" -e "SERVICE_PORT=3333" -p 3333:8080 -d descartesresearch/teastore-recommender`
4. Image Provider
   `docker run -v /home/myUserName/myFolder:/kieker/logs/ -e "LOG_TO_FILE=true" -e "REGISTRY_HOST=10.1.2.3" -e "REGISTRY_PORT=10000" -e "HOST_NAME=10.1.2.30" -e "SERVICE_PORT=4444" -p 4444:8080 -d descartesresearch/teastore-image`
5. WebUI
   `docker run -v /home/myUserName/myFolder:/kieker/logs/ -e "LOG_TO_FILE=true" -e "REGISTRY_HOST=10.1.2.3" -e "REGISTRY_PORT=10000" -e "HOST_NAME=10.1.2.30" -e "SERVICE_PORT=8080" -p 8080:8080 -d descartesresearch/teastore-webui`

The log files are available in the folder of the mounted volume (here /home/myUserName/myFolder) on the host machines. To run analysis on the generated Kieker traces, follow the documentation [here](http://eprints.uni-kiel.de/16537/67/kieker-1.12-userguide.pdf).

##### 2.2.1.4. Parameter Logging

Teastore offers the possibility to log the values of method call parameters as well as return values using custom kieker probes. As this feature is disabled by default, pass the LOG_PARAMETERS=true flag on container startup in order to enable it. For example:
   `docker run -v /home/myUserName/myFolder:/kieker/logs/ -e "LOG_PARAMETERS=true" -e "LOG_TO_FILE=true" -e "REGISTRY_HOST=10.1.2.3" -e "REGISTRY_PORT=10000" -e "HOST_NAME=10.1.2.30" -e "SERVICE_PORT=4444" -e "DB_HOST=10.1.2.20" -e "DB_PORT=3306" -p 4444:8080 -d descartesresearch/teastore-persistence`

#### 2.2.2. OpenTracing with Kubernetes and Istio

The TeaStore supports OpenTracing when used in a Kubernetes Cluster that uses Istio as service mesh.

## 3. Building and Customizing the TeaStore

1. If not already done, use the following command to clone the repository:

```bash
$ git clone https://github.com/DescartesResearch/TeaStore.git
```

2. Open the local project with a code editor of your choice
3. After you are finished with your changes, build the whole project using the following command:

```bash
$ mvn clean install
```

Alternatively, if you want to skip the tests to shorten build time use the following:

```bash
$ mvn clean install -DskipTests
```

4. To build all docker images in one go we provide a simple script. The script can be used as follows:

```bash
$ cd tools/
$ ./build_docker.sh
```

5. Deploy the TeaStore using one of the methods above, but adapt them to the locations of your custom containers.
