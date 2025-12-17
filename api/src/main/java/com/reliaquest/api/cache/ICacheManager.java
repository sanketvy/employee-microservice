package com.reliaquest.api.cache;

import com.reliaquest.api.dto.response.EmployeeResponse;

import java.util.List;

public interface ICacheManager {

    List<EmployeeResponse> getEmployees();

    void setEmployees(List<EmployeeResponse> employees);

    void invalidateCache();
}
