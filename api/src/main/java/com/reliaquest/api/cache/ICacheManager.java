package com.reliaquest.api.cache;

import com.reliaquest.api.dto.response.EmployeeResponse;

import java.util.List;

/**
 * Primary caching manager interface.
 * In order to implement different caching solutions like ehcache, redis, memcached just create new implementation
 */
public interface ICacheManager {

    List<EmployeeResponse> getEmployees();

    void setEmployees(List<EmployeeResponse> employees);

    void invalidateCache();
}
