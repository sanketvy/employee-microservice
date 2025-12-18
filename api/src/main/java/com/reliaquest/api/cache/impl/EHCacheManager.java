package com.reliaquest.api.cache.impl;

import lombok.extern.slf4j.Slf4j;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import com.reliaquest.api.cache.ICacheManager;
import com.reliaquest.api.dto.response.EmployeeResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class EHCacheManager implements ICacheManager {

    private final Cache<String, List<EmployeeResponse>> cache;

    private static final String EMPLOYEES = "employees";

    private static final String EMPLOYEES_CACHE = "employees_cache";

    public EHCacheManager(CacheManager cacheManager) {
        log.debug("Cache Manager using EHCache initialized");
        this.cache = cacheManager.getCache(
                EMPLOYEES_CACHE,
                String.class,
                (Class<List<EmployeeResponse>>) (Class<?>) List.class
        );
    }


    @Override
    public List<EmployeeResponse> getEmployees() {
        return cache.get(EMPLOYEES);
    }

    @Override
    public void setEmployees(List<EmployeeResponse> employees) {
        cache.put(EMPLOYEES, employees);
    }

    @Override
    public void invalidateCache() {
        cache.remove(EMPLOYEES);
    }
}
