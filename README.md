# TeaStore #  

The TeaStore is a micro-service reference and test application to be used in benchmarks and tests. The TeaStore emulates a basic web store for automatically generated, tea and tea supplies. As it is primarily a test application, it features UI elements for database generation and service resetting in addition to the store itself.

The TeaStore is a distributed micro-service application featuring five distinct services plus a registry. Each service may be replicated without limit and deployed on separate devices as desired. Services communicate using REST and using the Netflix [Ribbon](https://github.com/Netflix/ribbon) client side load balancer. Each service also comes in a pre-instrumented variant that uses [Kieker](http://kieker-monitoring.net) to provide detailed information about the TeaStore's actions and behavior.

Check out our [Getting Started Guide](GET_STARTED.md) for information on how to use the TeaStore:

1. [Deploying the TeaStore](GET_STARTED.md#1-deploying-the-teastore)
   1. [Run as Multiple Single Service Containers](GET_STARTED.md#11-run-as-multiple-single-service-containers)
   2. [Run the TeaStore using Docker Compose](GET_STARTED.md#12-run-the-teastore-using-docker-compose)
   3. [Run the TeaStore on a Kubernetes Cluster](GET_STARTED.md#13-run-the-teastore-on-a-kubernetes-cluster)
   4. [Run the TeaStore with helm templates](GET_STARTED.md#14-run-the-teastore-with-helm-templates)
2. [Using the TeaStore for Testing and Benchmarking](GET_STARTED.md#2-using-the-teastore-for-testing-and-benchmarking)
   1. [Generating Load](GET_STARTED.md#21-generating-load)
      1. [LIMBO HTTP Load Generator](GET_STARTED.md#211-limbo-http-load-generator)
      2. [JMeter™](GET_STARTED.md#212-jmeter)
   2. [Instrumenting the TeaStore](GET_STARTED.md#22-instrumenting-the-teastore)
      1. [Docker containers with Kieker](#221-docker-containers-with-kieker)
      2. [OpenTracing with Kubernetes and Istio](GET_STARTED.md#222-opentracing-with-kubernetes-and-istio)
3. [Building and Customizing the TeaStore](GET_STARTED.md#3-building-and-customizing-the-teastore)

## Cite Us

The TeaStore was first published in [Proceedings of the 26th IEEE International Symposium on the Modelling, Analysis, and Simulation of Computer and Telecommunication Systems (MASCOTS2018)](https://ieeexplore.ieee.org/document/8526888). If you use the TeaStore please cite the following publication:

    @inproceedings{KiEiScBaGrKo2018-MASCOTS-TeaStore,
      author = {J{\'o}akim von Kistowski and Simon Eismann and Norbert Schmitt and Andr{\'e} Bauer and Johannes Grohmann and Samuel Kounev},
      title = {{TeaStore: A Micro-Service Reference Application for Benchmarking, Modeling and Resource Management Research}},
      booktitle = {Proceedings of the 26th IEEE International Symposium on the Modelling, Analysis, and Simulation of Computer and Telecommunication Systems},
      series = {MASCOTS '18},
      year = {2018},
      month = {September},
      location = {Milwaukee, WI, USA},
    }

For an example of a large-scale TeaStore setup we refer to [Microservices: A Performance Tester’s Dream or Nightmare?](https://doi.org/10.1145/3358960.3379124) and the corresponding [replication package](https://doi.org/10.5281/zenodo.3582707).

    @inproceedings{10.1145/3358960.3379124,
      author = {Eismann, Simon and Bezemer, Cor-Paul and Shang, Weiyi and Okanovi\'{c}, Du\v{s}an and van Hoorn, Andr\'{e}},
      title = {Microservices: A Performance Tester's Dream or Nightmare?},
      year = {2020},
      booktitle = {Proceedings of the ACM/SPEC International Conference on Performance Engineering},
      pages = {138–149},
      series = {ICPE '20},
    }

 ## The TeaStore in Action
 The TeaStore is used as the demo application in the [Cisco Full Stack Observability Workshop](https://www.fsolabs.net/) and as a case study in a number of scientific publications:
 * A. Horn, H.M. Fard, F. Wolf. *Multi-objective Hybrid Autoscaling of Microservices in Kubernetes Clusters*. In Euro-Par 2022: Parallel Processing, pp 233–250. 2022. https://doi.org/10.1007/978-3-031-12597-3_15
 * M. Elsaadawy, A. Lohner, R. Wang, J. Wang, and B. Kemme. *DyMonD: dynamic application monitoring and service detection framework*. In Proceedings of the 22nd International Middleware Conference: Demos and Posters, pp- 8-9. 2021. https://doi.org/10.1145/3491086.3492471
 * D. Sokolowski, P. Weisenburger, and G. Salvaneschi. *Automating serverless deployments for DevOps organizations.* In ESEC/FSE 2021: Proceedings of the 29th ACM Joint Meeting on European Software Engineering Conference and Symposium on the Foundations of Software Engineering, pp. 57-69. 2021. https://doi.org/10.1145/3468264.3468575
* L. Liao, J. Chen,  H. Li, Y. Zeng, W. Shang, C. Sporea, A. Toma, and S. Sajedi. *Locating Performance Regression Root Causes in the Field Operations of Web-based Systems: An Experience Report.* In IEEE Transactions on Software Engineering. 2021. https://doi.org/10.1109/TSE.2021.3131529
* J. Flora, P. Gonçalves, M. Teixeira, and N. Antunes. *My Services Got Old! Can Kubernetes Handle the Aging of Microservices?* In IEEE International Symposium on Software Reliability Engineering Workshops. 2021. https://doi.org/10.1109/ISSREW53611.2021.00042
 * M. Torquato, P. Maciel, and M. Vieira. *PyMTDEvaluator: A Tool for Time-Based Moving Target Defense Evaluation: Tool description paper. In IEEE 32nd International Symposium on Software Reliability Engineering*. 2021. https://doi.org/10.1109/ISSRE52982.2021.00045
 * J. Keim, S. Schulz, D. Fuchß, C. Kocher, J. Speit, and A. Koziolek. *Trace Link Recovery for Software Architecture Documentation.* In European Conference on Software Architecture, pp. 101-116. 2021. https://doi.org/10.1007/978-3-030-86044-8_7
 * J. Grohmann, M. Straesser, A. Chalbani, S. Eismann, Y. Arian, N. Herbst, N Peretz, and S. Kounev. 2021. *SuanMing: Explainable Prediction of Performance Degradations in Microservice Applications.* In Proceedings of the ACM/SPEC International Conference on Performance Engineering, pp. 165-176. 2021. https://doi.org/10.1145/3427921.3450248
 * V. Rao, V. Singh, K. S. Goutham, B. U. Kempaiah, R. J. Mampilli, S. Kalambur, and D. Sitaram. 2021. *Scheduling Microservice Containers on Large Core Machines through Placement and Coalescing.*https://jsspp.org/papers21/vishal-rao.pdf
 * D. Monschein, M. Mazkatli, R. Heinrich, and A. Koziolek. 2021. *nabling Consistency between Software Artefacts for Software Adaption and Evolution.*In 2021 IEEE 18th International Conference on Software Architecture (ICSA) (pp. 1-12). https://sdqweb.ipd.kit.edu/publications/pdfs/monschein2021a.pdf
 * S. Eismann, C. Bezemer, W. Shang, D. Okanović, and A. van Hoorn. 2020. *icroservices: A Performance Tester's Dream or Nightmare?*In Proceedings of the ACM/SPEC International Conference on Performance Engineering (ICPE '20). Association for Computing Machinery, New York, NY, USA, 138–149. https://doi.org/10.1145/3358960.3379124
 * J. Grohmann, P. Nicholson, J. Iglesias, S. Kounev, and D. Lugones. 2019. *onitorless: Predicting Performance Degradation in Cloud Applications with Machine Learning.*In Proceedings of the 20th International Middleware Conference (Middleware '19). Association for Computing Machinery, New York, NY, USA, 149–162. https://doi.org/10.1145/3361525.3361543
 * M. Mazkatli, D. Monschein, J. Grohmann and A. Koziolek, *Incremental Calibration of Architectural Performance Models with Parametric Dependencies* 020 IEEE International Conference on Software Architecture (ICSA '2020), Salvador, Brazil, 2020, pp. 23-34, https://doi.org/10.1109/ICSA47634.2020.00011.
 * J. Grohmann, S. Eismann, S. Elflein, J. V. Kistowski, S. Kounev and M. Mazkatli, *Detecting Parametric Dependencies for Performance Models Using Feature Selection Techniques* 2019 IEEE 27th International Symposium on Modeling, Analysis, and Simulation of Computer and Telecommunication Systems (MASCOTS '19), Rennes, France, 2019, pp. 309-322, https://doi.org/10.1109/MASCOTS.2019.00042
* S. Gholami, A. Goli, C. Bezemer, and H. Khazaei. 2020. *A Framework for Satisfying the Performance Requirements of Containerized Software Systems Through Multi-Versioning.* In Proceedings of the ACM/SPEC International Conference on Performance Engineering (ICPE '20). Association for Computing Machinery, New York, NY, USA, 150–160. https://doi.org/10.1145/3358960.3379125
* N. Schmitt, L. Iffländer, A. Bauer and S. Kounev, *Online Power Consumption Estimation for Functions in Cloud Applications.* 2019 IEEE International Conference on Autonomic Computing (ICAC '19), Umea, Sweden, 2019, pp. 63-72, https://10.1109/ICAC.2019.00018
* S. Athlur, N. Sondhi, S. Batra, S. Kalambur and D. Sitaram, *Cache Characterization of Workloads in a Microservice Environment* 2019 IEEE International Conference on Cloud Computing in Emerging Markets (CCEM '19), Bengaluru, India, 2019, pp. 45-50, https://10.1109/CCEM48484.2019.00010
* S. Caculo, K. Lahiri and S. Kalambur, *Characterizing the Scale-Up Performance of Microservices using TeaStore.* 2020 IEEE International Symposium on Workload Characterization (IISWC '2020), Beijing, China, 2020, pp. 48-59, https://10.1109/IISWC50251.2020.00014
* A. Goli, N. Mahmoudi, H. Khazaei, and O. Ardakanian. *A Holistic Machine Learning-Based Autoscaling Approach for Microservice Applications.* [preprint](https://www.researchgate.net/profile/Alireza-Goli-2/publication/349550949_A_Holistic_Machine_Learning-Based_Autoscaling_Approach_for_Microservice_Applications/links/6035f80092851c4ed591298d/A-Holistic-Machine-Learning-Based-Autoscaling-Approach-for-Microservice-Applications.pdf)
* W. Viktorsson, C. Klein and J. Tordsson, *Security-Performance Trade-offs of Kubernetes Container Runtimes.* 2020 28th International Symposium on Modeling, Analysis, and Simulation of Computer and Telecommunication Systems (MASCOTS '2020), Nice, France, 2020, pp. 1-4, doi: https://10.1109/10.1109/MASCOTS50786.2020.9285946
* J. Martin, A. Kandasamy, and K. Chandrasekaran. *CREW: Cost and Reliability aware Eagle‐Whale optimiser for service placement in Fog.* Software: Practice and Experience 50.12 (2020): 2337-2360. https://doi.org/10.1002/spe.2896
* M. Tamiru, J. Tordsson, E. Elmroth, and G. Pierre. *An Experimental Evaluation of the Kubernetes Cluster Autoscaler in the Cloud.* In CloudCom 2020-12th IEEE International Conference on Cloud Computing Technology and Science. 2020. https://hal.inria.fr/hal-02958916
* E. Boza, C. Abad, S. Narayanan, B. Balasubramanian, and M. Jang. 2019. *A Case for Performance-Aware Deployment of Containers.* In Proceedings of the 5th International Workshop on Container Technologies and Container Clouds (WOC '19). Association for Computing Machinery, New York, NY, USA, 25–30. https://doi.org/10.1145/3366615.3368355

If your paper is missing from this list, open up an issue and we'll add it :)
