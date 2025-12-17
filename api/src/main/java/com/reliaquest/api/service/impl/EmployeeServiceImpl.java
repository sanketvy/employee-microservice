package com.reliaquest.api.service.impl;

import com.reliaquest.api.dto.request.EmployeeRequest;
import com.reliaquest.api.dto.response.EmployeeResponse;
import com.reliaquest.api.exception.NoDataFoundException;
import com.reliaquest.api.external.IExternalService;
import com.reliaquest.api.service.IEmployeeService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements IEmployeeService {

    IExternalService externalService;

    public EmployeeServiceImpl(IExternalService externalService){
        this.externalService = externalService;
    }

    @Override
    public List<EmployeeResponse> getAllEmployees() {
        return externalService.getAllEmployees();
    }

    @Override
    public List<EmployeeResponse> getEmployeesByName(String searchString) {
        List<EmployeeResponse> employeeResponses = externalService.getAllEmployees();
        String searchStringLowerCase = searchString.toLowerCase();
        return employeeResponses.stream().filter(employee -> employee.getEmployeeName().toLowerCase().contains(searchStringLowerCase)).toList();
    }

    @Override
    public EmployeeResponse getEmployeeById(String id) {
        return externalService.getEmployeeById(id);
    }

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

    @Override
    public EmployeeResponse createEmployee(EmployeeRequest employeeInput) {
        return externalService.createEmployee(employeeInput);
    }

    @Override
    public String deleteEmployee(String id) {
        EmployeeResponse response = externalService.getEmployeeById(id);
        externalService.deleteEmployee(response.getEmployeeName());
        return response.getEmployeeName();
    }
}
