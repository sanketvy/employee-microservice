package com.reliaquest.api.service;

import com.reliaquest.api.dto.response.EmployeeResponse;
import com.reliaquest.api.exception.BadRequestException;
import com.reliaquest.api.external.IExternalService;
import com.reliaquest.api.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceImplTest {

    @Mock
    private IExternalService externalService;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Test
    public void getAllEmployees_WhenEmployeesExist_ReturnListOfEmployees(){
        when(externalService.getAllEmployees()).thenReturn(getMockData());

        List<EmployeeResponse> response = employeeService.getAllEmployees();

        assertEquals("Sanket", response.get(0).getEmployeeName());
        assertEquals("srvyawahare@gmail.com", response.get(0).getEmployeeEmail());
        assertEquals(3, response.size());
    }

    @Test
    public void getAllEmployees_WhenEmployeesEmpty_ReturnListOfEmployees(){
        when(externalService.getAllEmployees()).thenReturn(new ArrayList<>());

        List<EmployeeResponse> response = employeeService.getAllEmployees();

        assertEquals(0, response.size());
    }

    @Test
    public void getEmployeesByName_WhenEmployeesExists_ReturnListOfEmployees(){
        when(externalService.getAllEmployees()).thenReturn(getMockData());

        List<EmployeeResponse> response = employeeService.getEmployeesByName("m");

        assertEquals(1, response.size());
    }

    @Test
    public void getEmployeesByName_WhenEmployeesNotExists_ReturnListOfEmployees() {
        when(externalService.getAllEmployees()).thenReturn(getMockData());

        List<EmployeeResponse> response = employeeService.getEmployeesByName("incorrect");

        assertEquals(0, response.size());
    }

    private List<EmployeeResponse> getMockData(){
        return List.of(
                EmployeeResponse.builder()
                        .id("1")
                        .employeeName("Sanket")
                        .employeeTitle("Software Developer")
                        .employeeEmail("srvyawahare@gmail.com")
                        .employeeSalary(500000)
                        .employeeAge(26)
                        .build(),
                EmployeeResponse.builder()
                        .id("2")
                        .employeeName("Shubham")
                        .employeeTitle("Software Developer 1")
                        .employeeEmail("shubham@gmail.com")
                        .employeeSalary(10000)
                        .employeeAge(26)
                        .build(),
                EmployeeResponse.builder()
                        .id("2")
                        .employeeName("Dnyanesh")
                        .employeeTitle("Data Engineer")
                        .employeeEmail("dnyanesh@gmail.com")
                        .employeeSalary(100000)
                        .employeeAge(26)
                        .build()
        );
    }
}
