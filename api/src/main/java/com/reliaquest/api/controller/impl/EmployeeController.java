package com.reliaquest.api.controller.impl;

import com.reliaquest.api.controller.IEmployeeController;
import com.reliaquest.api.dto.request.EmployeeRequest;
import com.reliaquest.api.dto.response.EmployeeResponse;
import com.reliaquest.api.service.IEmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/reliaquest/api/v1/employee", produces = MediaType.APPLICATION_JSON_VALUE)
public class EmployeeController implements IEmployeeController<EmployeeResponse, EmployeeRequest> {

    IEmployeeService employeeService;

    @Autowired
    public EmployeeController(IEmployeeService employeeService){
        this.employeeService = employeeService;
    }

    /**
     * This method returns all the employees, available from the downstream server
     * @return List<EmployeeResponse>
     */
    @Override
    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.getAllEmployees());
    }

    /**
     * This method returns all the employees, whose name contains the searchString
     *
     * @param searchString
     * @return List<EmployeeResponse>
     */
    @Override
    @GetMapping("/search/{searchString}")
    public ResponseEntity<List<EmployeeResponse>> getEmployeesByNameSearch(@PathVariable String searchString) {
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.getEmployeesByName(searchString));
    }


    /**
     * This method returns the employee by employee id
     *
     * @param id
     * @return EmployeeResponse
     */
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.getEmployeeById(id));
    }

    /**
     * This method returns the highest salary from all employees
     *
     * @return Integer
     */
    @Override
    @GetMapping("/highestSalary")
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.getHighestSalary());
    }

    /**
     * This method returns the list of top 10 highest earning names of employees
     * @return List<String>
     */
    @Override
    @GetMapping("/topTenHighestEarningEmployeeNames")
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.getTopHighestEarningEmployeesNames(10));
    }

    @Override
    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(@RequestBody @Valid EmployeeRequest employeeInput) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.createEmployee(employeeInput));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployeeById(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.deleteEmployee(id));
    }
}
