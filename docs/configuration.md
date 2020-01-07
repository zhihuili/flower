# 配置

Flower使用yaml格式的文件进行配置，默认应用会读取classpath下的flower.yml配置文件。如果不存在，即使用Flower默认值。

## 示例

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

所有配置项都可以在FlowerConfig类中看到，具体如下：

- **name** 应用名称 
- **host** 应用对外暴露的IP
- **port** 应用对外暴露的端口
- **registry** 应用注册中心地址，是一个set集合，可以多个注册中心地址
- **basePackage** 扫描注解FlowerService的包名
- **parallelismMin** 扫描注解FlowerService的包名
- **parallelismMax** 扫描注解FlowerService的包名
- **parallelismFactor** 扫描注解FlowerService的包名

## RegistryConfig

RegistryConfig是注册中心的配置类，里面包含了配置注册中心需要的参数。具体如下：

- **protocol** 注册中心协议
- **host** 注册中心IP地址
- **port** 注册中心端口 
- **params** 注册中心参数 