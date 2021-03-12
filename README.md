# TeaStore #  

The TeaStore is a micro-service reference and test application to be used in benchmarks and tests. The TeaStore emulates a basic web store for automatically generated, tea and tea supplies. As it is primarily a test application, it features UI elements for database generation and service resetting in addition to the store itself. 

The TeaStore is a distributed micro-service application featuring five distinct services plus a registry. Each service may be replicated without limit and deployed on separate devices as desired. Services communicate using REST and using the Netflix [Ribbon](https://github.com/Netflix/ribbon) client side load balancer. Each service also comes in a pre-instrumented variant that uses [Kieker](http://kieker-monitoring.net) to provide detailed information about the TeaStore's actions and behavior.

Check out our **[wiki](https://github.com/DescartesResearch/TeaStore/wiki)** with information on how to [get started using/developing the TeaStore](https://github.com/DescartesResearch/TeaStore/wiki/Getting-Started), more information on the architecture and services of the Tea Store, guides on how to [run the TeaStore for benchmarking/testing](https://github.com/DescartesResearch/TeaStore/wiki/Testing-and-Benchmarking), and common [troubleshooting tips](https://github.com/DescartesResearch/TeaStore/wiki/Troubleshooting).
You can also watch a short demonstration of the TeaStore running in Kubernetes with its monitoring environment enabled on **[YouTube](https://www.youtube.com/watch?v=6OcSNrErzGE&feature=youtu.be)**.

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
 The TeaStore has been used as a case study in a number of scientific publications:
 * Simon Eismann, Cor-Paul Bezemer, Weiyi Shang, Dušan Okanović, and André van Hoorn. 2020. Microservices: A Performance Tester's Dream or Nightmare? In Proceedings of the ACM/SPEC International Conference on Performance Engineering (ICPE '20). Association for Computing Machinery, New York, NY, USA, 138–149. https://doi.org/10.1145/3358960.3379124
 * Johannes Grohmann, Patrick K. Nicholson, Jesus Omana Iglesias, Samuel Kounev, and Diego Lugones. 2019. Monitorless: Predicting Performance Degradation in Cloud Applications with Machine Learning. In Proceedings of the 20th International Middleware Conference (Middleware '19). Association for Computing Machinery, New York, NY, USA, 149–162. https://doi.org/10.1145/3361525.3361543
 * M. Mazkatli, D. Monschein, J. Grohmann and A. Koziolek, "Incremental Calibration of Architectural Performance Models with Parametric Dependencies," 2020 IEEE International Conference on Software Architecture (ICSA), Salvador, Brazil, 2020, pp. 23-34, https://doi.org/10.1109/ICSA47634.2020.00011.
 * J. Grohmann, S. Eismann, S. Elflein, J. V. Kistowski, S. Kounev and M. Mazkatli, "Detecting Parametric Dependencies for Performance Models Using Feature Selection Techniques," 2019 IEEE 27th International Symposium on Modeling, Analysis, and Simulation of Computer and Telecommunication Systems (MASCOTS), Rennes, France, 2019, pp. 309-322, https://doi.org/10.1109/MASCOTS.2019.00042
* Sara Gholami, Alireza Goli, Cor-Paul Bezemer, and Hamzeh Khazaei. 2020. A Framework for Satisfying the Performance Requirements of Containerized Software Systems Through Multi-Versioning. In Proceedings of the ACM/SPEC International Conference on Performance Engineering (ICPE '20). Association for Computing Machinery, New York, NY, USA, 150–160. https://doi.org/10.1145/3358960.3379125
* N. Schmitt, L. Iffländer, A. Bauer and S. Kounev, "Online Power Consumption Estimation for Functions in Cloud Applications," 2019 IEEE International Conference on Autonomic Computing (ICAC), Umea, Sweden, 2019, pp. 63-72, https://10.1109/ICAC.2019.00018
* S. Athlur, N. Sondhi, S. Batra, S. Kalambur and D. Sitaram, "Cache Characterization of Workloads in a Microservice Environment," 2019 IEEE International Conference on Cloud Computing in Emerging Markets (CCEM), Bengaluru, India, 2019, pp. 45-50, https://10.1109/CCEM48484.2019.00010
* S. Caculo, K. Lahiri and S. Kalambur, "Characterizing the Scale-Up Performance of Microservices using TeaStore," 2020 IEEE International Symposium on Workload Characterization (IISWC), Beijing, China, 2020, pp. 48-59, https://10.1109/IISWC50251.2020.00014
* Goli, Alireza, Nima Mahmoudi, Hamzeh Khazaei, and Omid Ardakanian. "A Holistic Machine Learning-Based Autoscaling Approach for Microservice Applications." [preprint](https://www.researchgate.net/profile/Alireza-Goli-2/publication/349550949_A_Holistic_Machine_Learning-Based_Autoscaling_Approach_for_Microservice_Applications/links/6035f80092851c4ed591298d/A-Holistic-Machine-Learning-Based-Autoscaling-Approach-for-Microservice-Applications.pdf)
* W. Viktorsson, C. Klein and J. Tordsson, "Security-Performance Trade-offs of Kubernetes Container Runtimes," 2020 28th International Symposium on Modeling, Analysis, and Simulation of Computer and Telecommunication Systems (MASCOTS), Nice, France, 2020, pp. 1-4, doi: https://10.1109/10.1109/MASCOTS50786.2020.9285946
* Paul Martin, John, A. Kandasamy, and K. Chandrasekaran. "CREW: Cost and Reliability aware Eagle‐Whale optimiser for service placement in Fog." Software: Practice and Experience 50.12 (2020): 2337-2360. https://doi.org/10.1002/spe.2896
* Tamiru, Mulugeta, Johan Tordsson, Erik Elmroth, and Guillaume Pierre. "An Experimental Evaluation of the Kubernetes Cluster Autoscaler in the Cloud." In CloudCom 2020-12th IEEE International Conference on Cloud Computing Technology and Science. 2020. https://hal.inria.fr/hal-02958916
* Edwin F. Boza, Cristina L. Abad, Shankaranarayanan Puzhavakath Narayanan, Bharath Balasubramanian, and Minsung Jang. 2019. A Case for Performance-Aware Deployment of Containers. In Proceedings of the 5th International Workshop on Container Technologies and Container Clouds (WOC '19). Association for Computing Machinery, New York, NY, USA, 25–30. https://doi.org/10.1145/3366615.3368355

If your paper is missing from this list, open up an issue and we'll add it :)
