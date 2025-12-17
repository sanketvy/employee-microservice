package com.reliaquest.api.external;

import com.reliaquest.api.dto.request.EmployeeRequest;
import com.reliaquest.api.dto.response.EmployeeResponse;

import java.util.List;

public interface IExternalService {

    public List<EmployeeResponse> getAllEmployees();

    EmployeeResponse getEmployeeById(String id);

    EmployeeResponse createEmployee(EmployeeRequest employeeInput);

    void deleteEmployee(String name);
}
