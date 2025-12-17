package com.reliaquest.api.service;

import com.reliaquest.api.dto.request.EmployeeRequest;
import com.reliaquest.api.dto.response.EmployeeResponse;

import java.util.List;

public interface IEmployeeService {

    List<EmployeeResponse> getAllEmployees();

    List<EmployeeResponse> getEmployeesByName(String searchString);

    EmployeeResponse getEmployeeById(String id);

    Integer getHighestSalary();

    List<String> getTopHighestEarningEmployeesNames(int size);

    EmployeeResponse createEmployee(EmployeeRequest employeeInput);

    String deleteEmployee(String id);
}
