# Pet Supply Store #

The Pet Supply Store is a micro-service reference and test application developed by the Descartes Research Group at the University of Würzburg. The Pet Supply Store emulates a basic web store for automatically generated, fictitious pet supplies. As it is primarily a test application, it features UI elements for database generation and service resetting in addition to the store itself. 

The Pet Supply Store is a distributed micro-service application featuring five distinct services plus a registry. Each service may be replicated without limit and deployed on separate devices as desired. Services communicate using REST and using the Netflix "[Ribbon](https://github.com/Netflix/ribbon)" client side load balancer.

The Pet Supply Store is designed to be a reference / test application to be used in benchmarks and tests. Some of its envisioned use-cases are:
* Testing performance model extractors and predictors for distributed applications
* Testing micro-service and cloud management mechanisms, such as multi-tier auto-scalers
* Testing energy efficiency and power prediction and management mechanisms

Check out our **[wiki](https://github.com/DescartesResearch/Pet-Supply-Store/wiki)** with information on how to [get started using/developing the Pet Supply Store](https://github.com/DescartesResearch/Pet-Supply-Store/wiki/Getting-Started), more information on the architecture and services of the Pet Supply Store, and guides on how to [run the Pet Supply Store for benchmarking/testing](https://github.com/DescartesResearch/Pet-Supply-Store/wiki/Testing-and-Benchmarking).

## Getting Started

To get started, we recommend running the Pet Supply Store in an environment of your choice. We offer three ways of deploying and running the Pet Supply Store:
* The easiest way: [Run Pet Supply Store Container(s) using Docker](https://github.com/DescartesResearch/Pet-Supply-Store/wiki/Getting-Started#run-pet-supply-store-containers-using-docker) (Recommended)
* [Deploy the Pet Supply Store in one or several Java Application Container(s)](https://github.com/DescartesResearch/Pet-Supply-Store/wiki/Getting-Started#deploy-the-pet-supply-store-in-java-application-containers)
* [Setup and run the Pet Supply Store Development Environment](https://github.com/DescartesResearch/Pet-Supply-Store/wiki/Getting-Started#setup-and-run-the-pet-supply-store-development-environment)
