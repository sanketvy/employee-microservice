package com.reliaquest.api.service.impl;

import com.reliaquest.api.dto.request.EmployeeRequest;
import com.reliaquest.api.dto.response.EmployeeResponse;
import com.reliaquest.api.external.IExternalService;
import com.reliaquest.api.service.IEmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Primary implementation of IEmployeeService
 */
@Service
@Slf4j
public class EmployeeServiceImpl implements IEmployeeService {

    IExternalService externalService;

    public EmployeeServiceImpl(IExternalService externalService){
        this.externalService = externalService;
    }

    /**
     * Returns all employees by calling external service
     * @return List<EmployeeResponse>
     */
    @Override
    public List<EmployeeResponse> getAllEmployees() {
        return externalService.getAllEmployees();
    }

    /**
     * Returns all employees by calling external service with searchString.
     * It will return employees whose name contains the searchString in lowercase
     *
     * @param searchString
     * @return List<EmployeeResponse>
     */
    @Override
    public List<EmployeeResponse> getEmployeesByName(String searchString) {
        List<EmployeeResponse> employeeResponses = externalService.getAllEmployees();
        String searchStringLowerCase = searchString.toLowerCase();
        return employeeResponses.stream().filter(employee -> employee.getEmployeeName().toLowerCase().contains(searchStringLowerCase)).toList();
    }

    /**
     * Returns single employee details by employee id
     *
     * @param id
     * @return EmployeeResponse
     */
    @Override
    public EmployeeResponse getEmployeeById(String id) {
        return externalService.getEmployeeById(id);
    }

    /**
     * Returns the highest salary available among all employees
     * @return Integer
     */
    @Override
    public Integer getHighestSalary() {
        int maxSalary = 0;

        List<EmployeeResponse> employeeResponses = externalService.getAllEmployees();
        for(EmployeeResponse employeeResponse: employeeResponses){
            if(employeeResponse.getEmployeeSalary() > maxSalary){
                maxSalary = employeeResponse.getEmployeeSalary();
            }
        }

        return maxSalary;
    }

    /**
     * Returns top N highest earning employees
     * @param size
     * @return List<String>
     */
    @Override
    public List<String> getTopHighestEarningEmployeesNames(int size) {
        List<EmployeeResponse> employeeResponses = externalService.getAllEmployees();

        Queue<EmployeeResponse> priorityHeap = new PriorityQueue<>((e1, e2) -> e1.getEmployeeSalary() - e2.getEmployeeSalary());

        for(EmployeeResponse employeeResponse: employeeResponses){
            priorityHeap.offer(employeeResponse);

            if(priorityHeap.size() > size){
                priorityHeap.poll();
            }
        }

        return priorityHeap.stream()
                .sorted((e1, e2) -> e2.getEmployeeSalary() - e1.getEmployeeSalary())
                .map(EmployeeResponse::getEmployeeName)
                .toList();
    }

    /**
     * Creates a new employee and returns the created entity back to user
     * @param employeeInput
     * @return EmployeeResponse
     */
    @Override
    public EmployeeResponse createEmployee(EmployeeRequest employeeInput) {
        return externalService.createEmployee(employeeInput);
    }

    /**
     * Deletes the employee by employee id
     * @param id
     * @return
     */
    @Override
    public String deleteEmployee(String id) {
        EmployeeResponse response = externalService.getEmployeeById(id);
        externalService.deleteEmployee(response.getEmployeeName());
        return response.getEmployeeName();
    }
}
