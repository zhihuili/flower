/**
 * 
 */
package com.ly.flower.center.config;

import java.time.Duration;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author leeyazhou
 *
 */
@Configuration
public class EhcacheConfig {

  public static void main(String[] args) throws InterruptedException {
    new EhcacheConfig().config();
  }

  @Bean
  public CacheManager config() throws InterruptedException {
    CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build();
    cacheManager.init();
    return cacheManager;
  }

  @Bean
  public Cache<String, String> flowerCache(CacheManager cacheManager) {
    Cache<String, String> flowerCache = cacheManager.createCache("flowerCache",
        CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.heap(10))
            .withExpiry(ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofSeconds(5)))
            .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(5))));
    return flowerCache;
  }
}
