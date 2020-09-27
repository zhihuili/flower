# spring-boot

## Installation 

maven

```xml
<dependency>
    <groupId>com.ly.train</groupId>
    <artifactId>flower.boot.starter</artifactId>
    <version>A.B.C</version>
</dependency>
```

## Configuration class

FlowerConfiguration.java

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.ly.train.flower.core.service.container.FlowerFactory;
import com.ly.train.flower.web.spring.container.SpringFlowerFactory;

/**
 * @author leeyazhou
 *
 */
@Configuration
public class FlowerConfiguration {

  @Bean
  public FlowerFactory flowerFactory() {
    return new SpringFlowerFactory();
  }
}
```

application.yml

```yml
flower:
   name: flower-boot-demo
   host: 127.0.0.1
   port: 25002
```

## Main class 

```java

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.ly.train.flower.web.spring.context.FlowerComponentScan;

@SpringBootApplication
@FlowerComponentScan(basePackages = { "com.ly.train.flower.web.spring", "com.ly.train.flower.sample.springboot" })
public class FlowerApplication {
  public static void main(String[] args) {
    SpringApplication.run(FlowerApplication.class, args);
  }
}
```
