# Flower, a reactive programming framework for microservices#


![Build Status](https://travis-ci.org/zhihuili/flower.svg?branch=master)
[![codecov](https://codecov.io/gh/zhihuili/flower/branch/master/graph/badge.svg)](https://codecov.io/gh/zhihuili/flower)
[![Percentage of issues still open](http://isitmaintained.com/badge/open/zhihuili/flower.svg)](http://isitmaintained.com/project/zhihuili/flower "Percentage of issues still open")
![license](https://img.shields.io/github/license/zhihuili/flower.svg)
[![star this repo](http://githubbadges.com/star.svg?user=zhihuili&repo=flower&style=flat)](https://github.com/zhihuili/flower)
[![fork this repo](http://githubbadges.com/fork.svg?user=zhihuili&repo=flower&style=flat)](https://github.com/zhihuili/flower/fork)

Flower is a reactive programming framework based on Akka. To create a reactive system with Flower, developers just need to create a service for each business requirement and build service flows based on the business flow with self-explanatory configuration files.  


* Immediate response: The calling service will receive the response immediately, no need to wait for the whole flow to execute. No more synchronous calls.
* Robust: Self-recover when during service crush or hardware failure.
* Scalable: Auto scale based on load. Control request accepting rate based on system processing power.    
* Message-drive: Services communicate via messages. No coupling between services except the message itself: the previous service's return value must be the next service's input. Flower will encapsulate the return value into a message and sent it to the next service. 

**Flower is both a reactive programming framework, and a distributed micro-services framework**

**Flower hides the complicity of implementing reactive system**

## Getting Started

Build a reactive programming system in 5 minutes with this [quick start guide](/docs/quick-start.en.md)

## Related documents

* [Build high performance, high availability system with Flower (in simplified Chinese)](/docs/反应式编程框架Flower.pdf)
* [Research Paper: Research and implementation of the next generation of reactive programming framework (in simplified Chinese)](/docs/论文《下一代的反应式编程框架研究与实现》.pdf)

## Join us 

[How to contribute to Flower ?](CONTRIBUTING.md)


## [Versions](https://semver.org)

Given a version number MAJOR.MINOR.PATCH, increment the:  
1. MAJOR version when you make incompatible API changes,
2. MINOR version when you add functionality in a backwards compatible manner, and
3. PATCH version when you make backwards compatible bug fixes.

Additional labels for pre-release and build metadata are available as extensions to the MAJOR.MINOR.PATCH format.

## License

Flower is released under the [Apache License 2.0](https://github.com/zhihuili/flower/blob/master/LICENSE.txt)
