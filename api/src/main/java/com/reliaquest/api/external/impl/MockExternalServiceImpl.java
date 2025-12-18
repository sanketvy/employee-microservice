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

    private final RestTemplate restTemplate;

    private final ICacheManager cacheManager;

    @Value("${mock.external.url}")
    private String externalServiceBasePath;

    public MockExternalServiceImpl(RestTemplate restTemplate, ICacheManager cacheManager){
        this.restTemplate = restTemplate;
        this.cacheManager = cacheManager;
    }

    /**
     * This method returns all the employees by calling mock service.
     * If the data is found in cache, then no API call will be made.
     *
     * @return List<EmployeeResponse>
     */
    @Override
    public List<EmployeeResponse> getAllEmployees(){
        if(cacheManager.getEmployees() != null && !cacheManager.getEmployees().isEmpty()){
            log.info("Cache hit: returning employees from cache");
            return cacheManager.getEmployees();
        }

        List<EmployeeResponse> employeeResponseList = new ArrayList<>();
        log.info("Cache miss: fetching employees from external service");
        try {
            log.debug("Calling external GET {}", externalServiceBasePath);

            ResponseEntity<ExternalResponseDTO<List<ExternalEmployeeResponseDTO>>> response = restTemplate.exchange(externalServiceBasePath, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
            if(response.getBody() == null){
                throw new NoDataFoundException("No Data Found.");
            }

            for(ExternalEmployeeResponseDTO externalResponseDTO : response.getBody().getData()){
                employeeResponseList.add(mapToEmployee(externalResponseDTO));
            }
        } catch (HttpClientErrorException.TooManyRequests ex){
            throw new TooManyRequestsException("Too Many Requests. Please try again");
        } catch (Exception ex){
            log.error("Error while fetching employees from external service", ex);
            throw new RuntimeException("Problem Connecting External System. Please try again.");
        }

        cacheManager.setEmployees(employeeResponseList);
        return employeeResponseList;
    }

    /**
     * This method returns the employee by employee_id by calling mock service.
     *
     * @return EmployeeResponse
     */
    @Override
    public EmployeeResponse getEmployeeById(String id){
        try {
            ResponseEntity<ExternalResponseDTO<ExternalEmployeeResponseDTO>> response = restTemplate.exchange(externalServiceBasePath + "/" + id, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

            if(response.getBody() == null || response.getBody().getData() == null){
                throw new NoDataFoundException("No Data Found for employee id :" + id);
            }
            return mapToEmployee(response.getBody().getData());
        } catch (HttpClientErrorException.TooManyRequests ex){
            throw new TooManyRequestsException("Too Many Requests. Please try again");
        } catch (HttpClientErrorException.NotFound ex){
            throw new BadRequestException("No Data Found for employee id : " + id);
        } catch (Exception ex){
            log.error("Error while fetching employee from external service", ex);
            throw new RuntimeException("Problem Connecting External System. Please try again.");
        }
    }

    /**
     * This method creates a new user based on input request
     * Once user is created, in memory cache is invalidated
     *
     * @param employeeInput
     * @return EmployeeResponse
     */
    @Override
    public EmployeeResponse createEmployee(EmployeeRequest employeeInput) {
        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<EmployeeRequest> entity =
                    new HttpEntity<>(employeeInput, headers);

            ResponseEntity<ExternalResponseDTO<ExternalEmployeeResponseDTO>> response = restTemplate.exchange(externalServiceBasePath, HttpMethod.POST, entity, new ParameterizedTypeReference<>() {});

            if(response.getBody() == null || response.getBody().getData() == null){
                throw new RuntimeException("Error Creating Entity");
            }
            // invalidate cache
            cacheManager.invalidateCache();
            return mapToEmployee(response.getBody().getData());
        } catch (HttpClientErrorException.TooManyRequests ex){
            throw new TooManyRequestsException("Too Many Requests. Please try again");
        } catch (Exception ex){
            log.error("Error creating employee from external service", ex);
            throw new BadRequestException(ex.getMessage());
        }
    }

    /**
     * This method deletes and employee by its name and returns the deleted employees name
     * @param name
     */
    @Override
    public void deleteEmployee(String name) {
        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<ExternalDeleteEmployeeDTO> entity =
                    new HttpEntity<>(new ExternalDeleteEmployeeDTO(name), headers);

            ResponseEntity<ExternalResponseDTO<Boolean>> response = restTemplate.exchange(externalServiceBasePath, HttpMethod.DELETE, entity, new ParameterizedTypeReference<>() {});

            if(response.getBody() == null || response.getBody().getData() == false){
                throw new RuntimeException("Error Creating Entity");
            }

            // invalidate cache
            cacheManager.invalidateCache();
        } catch (HttpClientErrorException.TooManyRequests ex){
            throw new TooManyRequestsException("Too Many Requests. Please try again");
        } catch (Exception ex){
            log.error("Error deleting employee from external service", ex);
            throw new BadRequestException("Invalid Data. Please use correct data.");
        }
    }

    /**
     * Helper method for DTO conversion
     * @param dto
     * @return
     */
    private EmployeeResponse mapToEmployee(ExternalEmployeeResponseDTO dto) {
        return EmployeeResponse.builder()
                .id(dto.getId())
                .employeeAge(dto.getEmployeeAge())
                .employeeEmail(dto.getEmployeeEmail())
                .employeeName(dto.getEmployeeName())
                .employeeSalary(dto.getEmployeeSalary())
                .employeeTitle(dto.getEmployeeTitle())
                .build();
    }
}

