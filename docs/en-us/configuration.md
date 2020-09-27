# Configure Flower

Flower is configured using yaml files. Flower will try load configuration file from classpath/flower.yml. If no configuration file is present, Flower will use default configurations. 

You can also specify the path to configuration files are following:

```java
    FlowerFactory flowerFactory = new SimpleFlowerFactory("conf/flower.yml");
    flowerFactory.start();
```

## Example 

flower.yml

```yaml
name: "LocalFlower"
host: "127.0.0.1"
port: 25005
registry:
  - url: "redis://127.0.0.1:6399?password=flowerpassword"
basePackage: com.ly.train.flower
```

## FlowerConfig

You can find out configurable items in FlowerConfig class.

- **name**: Name of the application. 
- **host**: Service's IP address.
- **port**: Service's port number.
- **registry**: A collection of service registry's addresses. Can contain multiple values.
- **basePackage** Name of the base package of FlowerService. 
- **parallelismMin** Minium number of concurrent processes.
- **parallelismMax** Maximum number of concurrent processes.
- **parallelismFactor** Factor for maximum number of processes. Concurrent process number=available processors * parallelismFactor

## RegistryConfig

RegistryConfig is the configuration class for service registry, which contains the following keys:

- **protocol**: Protocol of service registry .
- **host** IP of service registry. 
- **port** Port number of service registry.
- **params** Parameters for service registry. 
