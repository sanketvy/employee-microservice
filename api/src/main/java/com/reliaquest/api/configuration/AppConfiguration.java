package com.reliaquest.api.configuration;

import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
public class AppConfiguration {

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Bean
    public CacheManager ehCacheManager() {

        return CacheManagerBuilder.newCacheManagerBuilder()
                .withCache(
                        "employees_cache",
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(
                                String.class,
                                (Class<List<?>>) (Class<?>) List.class,
                                ResourcePoolsBuilder.heap(10)
                        )
                )
                .build(true);
    }

}
