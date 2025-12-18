package com.reliaquest.api.service;

import com.reliaquest.api.cache.ICacheManager;
import com.reliaquest.api.dto.request.EmployeeRequest;
import com.reliaquest.api.dto.response.EmployeeResponse;
import com.reliaquest.api.external.IExternalService;
import com.reliaquest.api.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    ICacheManager cacheManager;

    @Value("${mock.external.url}")
    private String externalServiceBasePath;

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

    @Test
    public void getHighestSalary_WhenEmployeesExists_ReturnHighestSalary() {
        when(externalService.getAllEmployees()).thenReturn(getMockData());

        Integer response = employeeService.getHighestSalary();

        assertEquals(500000, response);
    }

    @Test
    public void getTopHighestEarningEmployeesNames_WhenEmployeesExists_ValidateTop10() {
        when(externalService.getAllEmployees()).thenReturn(getMockDataForTop10());

        List<String> response = employeeService.getTopHighestEarningEmployeesNames(10);

        assertEquals("Sanket", response.get(0));
        assertEquals("Arjun", response.get(1));
        assertEquals(10, response.size());
    }

    @Test
    public void createEmployee_WhenEmployeesExists_validateNewEmployee() {
        EmployeeRequest employeeRequest = new EmployeeRequest("Sanket", 20000, 26, "SDE 2");

        when(externalService.createEmployee(employeeRequest)).thenReturn(EmployeeResponse.builder()
                        .employeeName("Sanket")
                        .employeeSalary(20000)
                        .employeeAge(26)
                        .employeeTitle("SDE 2")
                .build());

        EmployeeResponse response = employeeService.createEmployee(employeeRequest);

        assertNotNull(response);
        assertEquals("Sanket", response.getEmployeeName());
        assertEquals(20000, response.getEmployeeSalary());
    }

    @Test
    public void deleteEmployee_WhenEmployeesExists_ReturnDeletedEmployeeName() {
        String id = "1";
        when(externalService.getEmployeeById(id)).thenReturn(EmployeeResponse.builder()
                .employeeName("Sanket")
                .employeeSalary(20000)
                .employeeAge(26)
                .employeeTitle("SDE 2")
                .id(id)
                .build());

        String response = employeeService.deleteEmployee(id);

        assertNotNull(response);
        assertEquals("Sanket", response);
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

    private List<EmployeeResponse> getMockDataForTop10() {
        return List.of(
                EmployeeResponse.builder()
                        .id("1")
                        .employeeName("Sanket")
                        .employeeTitle("Software Developer")
                        .employeeEmail("sanket@gmail.com")
                        .employeeSalary(500_000_000)
                        .employeeAge(26)
                        .build(),

                EmployeeResponse.builder()
                        .id("2")
                        .employeeName("Shubham")
                        .employeeTitle("Software Developer I")
                        .employeeEmail("shubham@gmail.com")
                        .employeeSalary(100_000)
                        .employeeAge(26)
                        .build(),

                EmployeeResponse.builder()
                        .id("3")
                        .employeeName("Dnyanesh")
                        .employeeTitle("Data Engineer")
                        .employeeEmail("dnyanesh@gmail.com")
                        .employeeSalary(300_000)
                        .employeeAge(27)
                        .build(),

                EmployeeResponse.builder()
                        .id("4")
                        .employeeName("Amit")
                        .employeeTitle("Backend Engineer")
                        .employeeEmail("amit@gmail.com")
                        .employeeSalary(250_000)
                        .employeeAge(28)
                        .build(),

                EmployeeResponse.builder()
                        .id("5")
                        .employeeName("Rahul")
                        .employeeTitle("Frontend Engineer")
                        .employeeEmail("rahul@gmail.com")
                        .employeeSalary(150_000)
                        .employeeAge(25)
                        .build(),

                EmployeeResponse.builder()
                        .id("6")
                        .employeeName("Neha")
                        .employeeTitle("QA Engineer")
                        .employeeEmail("neha@gmail.com")
                        .employeeSalary(90_000)
                        .employeeAge(26)
                        .build(),

                EmployeeResponse.builder()
                        .id("7")
                        .employeeName("Pooja")
                        .employeeTitle("DevOps Engineer")
                        .employeeEmail("pooja@gmail.com")
                        .employeeSalary(350_000)
                        .employeeAge(29)
                        .build(),

                EmployeeResponse.builder()
                        .id("8")
                        .employeeName("Kunal")
                        .employeeTitle("Cloud Engineer")
                        .employeeEmail("kunal@gmail.com")
                        .employeeSalary(280_000)
                        .employeeAge(27)
                        .build(),

                EmployeeResponse.builder()
                        .id("9")
                        .employeeName("Rohit")
                        .employeeTitle("System Engineer")
                        .employeeEmail("rohit@gmail.com")
                        .employeeSalary(120_000)
                        .employeeAge(26)
                        .build(),

                EmployeeResponse.builder()
                        .id("10")
                        .employeeName("Anjali")
                        .employeeTitle("Product Engineer")
                        .employeeEmail("anjali@gmail.com")
                        .employeeSalary(400_000)
                        .employeeAge(28)
                        .build(),

                EmployeeResponse.builder()
                        .id("11")
                        .employeeName("Vikas")
                        .employeeTitle("Tech Lead")
                        .employeeEmail("vikas@gmail.com")
                        .employeeSalary(600_000)
                        .employeeAge(32)
                        .build(),

                EmployeeResponse.builder()
                        .id("12")
                        .employeeName("Sneha")
                        .employeeTitle("Architect")
                        .employeeEmail("sneha@gmail.com")
                        .employeeSalary(700_000)
                        .employeeAge(34)
                        .build(),

                EmployeeResponse.builder()
                        .id("13")
                        .employeeName("Arjun")
                        .employeeTitle("Engineering Manager")
                        .employeeEmail("arjun@gmail.com")
                        .employeeSalary(800_000)
                        .employeeAge(36)
                        .build(),

                EmployeeResponse.builder()
                        .id("14")
                        .employeeName("Nikhil")
                        .employeeTitle("Senior Developer")
                        .employeeEmail("nikhil@gmail.com")
                        .employeeSalary(450_000)
                        .employeeAge(30)
                        .build(),

                EmployeeResponse.builder()
                        .id("15")
                        .employeeName("Priya")
                        .employeeTitle("ML Engineer")
                        .employeeEmail("priya@gmail.com")
                        .employeeSalary(320_000)
                        .employeeAge(29)
                        .build()
        );
    }

}
