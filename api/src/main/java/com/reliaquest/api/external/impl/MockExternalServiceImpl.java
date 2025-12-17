package com.reliaquest.api.external.impl;

import com.reliaquest.api.cache.ICacheManager;
import com.reliaquest.api.dto.request.EmployeeRequest;
import com.reliaquest.api.dto.response.EmployeeResponse;
import com.reliaquest.api.exception.BadRequestException;
import com.reliaquest.api.exception.NoDataFoundException;
import com.reliaquest.api.exception.TooManyRequestsException;
import com.reliaquest.api.external.IExternalService;
import com.reliaquest.api.external.dto.ExternalDeleteEmployeeDTO;
import com.reliaquest.api.external.dto.ExternalEmployeeResponseDTO;
import com.reliaquest.api.external.dto.ExternalResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class MockExternalServiceImpl implements IExternalService {

    RestTemplate restTemplate;

    ICacheManager cacheManager;

    @Value("${mock.external.url}")
    private String externalServiceBasePath;

    public MockExternalServiceImpl(RestTemplate restTemplate, ICacheManager cacheManager){
        this.restTemplate = restTemplate;
        this.cacheManager = cacheManager;
    }

    @Override
    public List<EmployeeResponse> getAllEmployees(){
        if(cacheManager.getEmployees() != null && !cacheManager.getEmployees().isEmpty()){
            log.info("Returning list of employees from cache.");
            return cacheManager.getEmployees();
        }

        List<EmployeeResponse> employeeResponseList = new ArrayList<>();

        try {
            ResponseEntity<ExternalResponseDTO<List<ExternalEmployeeResponseDTO>>> response = restTemplate.exchange(externalServiceBasePath, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
            if(response.getBody() == null){
                throw new NoDataFoundException("No Data Found.");
            }

            for(ExternalEmployeeResponseDTO externalResponseDTO : response.getBody().getData()){
                employeeResponseList.add(EmployeeResponse.builder()
                        .id(externalResponseDTO.getId())
                        .employeeAge(externalResponseDTO.getEmployeeAge())
                        .employeeEmail(externalResponseDTO.getEmployeeEmail())
                        .employeeName(externalResponseDTO.getEmployeeName())
                        .employeeSalary(externalResponseDTO.getEmployeeSalary())
                        .employeeTitle(externalResponseDTO.getEmployeeTitle())
                        .build());
            }
        } catch (HttpClientErrorException.TooManyRequests ex){
            throw new TooManyRequestsException("Too Many Requests. Please try again");
        } catch (Exception ex){
            throw new RuntimeException("Problem Connecting External System. Please try again.");
        }

        cacheManager.setEmployees(employeeResponseList);
        return employeeResponseList;
    }

    @Override
    public EmployeeResponse getEmployeeById(String id){
        try {
            ResponseEntity<ExternalResponseDTO<ExternalEmployeeResponseDTO>> response = restTemplate.exchange(externalServiceBasePath + "/" + id, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

            if(response.getBody() == null || response.getBody().getData() == null){
                throw new NoDataFoundException("No Data Found for employee id :" + id);
            }

            return EmployeeResponse.builder()
                        .id(response.getBody().getData().getId())
                        .employeeAge(response.getBody().getData().getEmployeeAge())
                        .employeeEmail(response.getBody().getData().getEmployeeEmail())
                        .employeeName(response.getBody().getData().getEmployeeName())
                        .employeeSalary(response.getBody().getData().getEmployeeSalary())
                        .employeeTitle(response.getBody().getData().getEmployeeTitle())
                        .build();

        } catch (HttpClientErrorException.TooManyRequests ex){
            throw new TooManyRequestsException("Too Many Requests. Please try again");
        } catch (HttpClientErrorException.NotFound ex){
            throw new BadRequestException("No Data Found for employee id : " + id);
        } catch (Exception ex){
            throw new RuntimeException("Problem Connecting External System. Please try again.");
        }
    }

    @Override
    public EmployeeResponse createEmployee(EmployeeRequest employeeInput) {
        cacheManager.invalidateCache();
        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<EmployeeRequest> entity =
                    new HttpEntity<>(employeeInput, headers);

            ResponseEntity<ExternalResponseDTO<ExternalEmployeeResponseDTO>> response = restTemplate.exchange(externalServiceBasePath, HttpMethod.POST, entity, new ParameterizedTypeReference<>() {});

            if(response.getBody() == null || response.getBody().getData() == null){
                throw new RuntimeException("Error Creating Entity");
            }

            return EmployeeResponse.builder()
                    .id(response.getBody().getData().getId())
                    .employeeAge(response.getBody().getData().getEmployeeAge())
                    .employeeEmail(response.getBody().getData().getEmployeeEmail())
                    .employeeName(response.getBody().getData().getEmployeeName())
                    .employeeSalary(response.getBody().getData().getEmployeeSalary())
                    .employeeTitle(response.getBody().getData().getEmployeeTitle())
                    .build();

        } catch (HttpClientErrorException.TooManyRequests ex){
            throw new TooManyRequestsException("Too Many Requests. Please try again");
        } catch (Exception ex){
            throw new BadRequestException(ex.getMessage());
        }
    }

    @Override
    public void deleteEmployee(String name) {
        cacheManager.invalidateCache();
        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<ExternalDeleteEmployeeDTO> entity =
                    new HttpEntity<>(new ExternalDeleteEmployeeDTO(name), headers);

            ResponseEntity<ExternalResponseDTO<Boolean>> response = restTemplate.exchange(externalServiceBasePath, HttpMethod.DELETE, entity, new ParameterizedTypeReference<>() {});

            if(response.getBody() == null || response.getBody().getData() == false){
                throw new RuntimeException("Error Creating Entity");
            }

        } catch (HttpClientErrorException.TooManyRequests ex){
            throw new TooManyRequestsException("Too Many Requests. Please try again");
        } catch (Exception ex){
            throw new BadRequestException("Invalid Data. Please use correct data.");
        }
    }

}

