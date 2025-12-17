package com.reliaquest.api.cache.impl;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import com.reliaquest.api.cache.ICacheManager;
import com.reliaquest.api.dto.response.EmployeeResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EHCacheManager implements ICacheManager {

    private final Cache<String, List<EmployeeResponse>> cache;

    public EHCacheManager(CacheManager cacheManager) {
        this.cache = cacheManager.getCache(
                "employees_cache",
                String.class,
                (Class<List<EmployeeResponse>>) (Class<?>) List.class
        );
    }


    @Override
    public List<EmployeeResponse> getEmployees() {
        return cache.get("employees");
    }

    @Override
    public void setEmployees(List<EmployeeResponse> employees) {
        cache.put("employees", employees);
    }

    @Override
    public void invalidateCache() {
        cache.remove("employees");
    }
}
