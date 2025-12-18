package com.reliaquest.api.external;

import com.reliaquest.api.cache.ICacheManager;
import com.reliaquest.api.dto.request.EmployeeRequest;
import com.reliaquest.api.dto.response.EmployeeResponse;
import com.reliaquest.api.exception.BadRequestException;
import com.reliaquest.api.exception.NoDataFoundException;
import com.reliaquest.api.exception.TooManyRequestsException;
import com.reliaquest.api.external.dto.ExternalDeleteEmployeeDTO;
import com.reliaquest.api.external.dto.ExternalEmployeeResponseDTO;
import com.reliaquest.api.external.dto.ExternalResponseDTO;
import com.reliaquest.api.external.impl.MockExternalServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class MockExternalServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ICacheManager cacheManager;

    @InjectMocks
    private MockExternalServiceImpl externalService;

    private static final String BASE_URL = "http://mock/api/employees";

    @BeforeEach
    void setup() {
        org.springframework.test.util.ReflectionTestUtils
                .setField(externalService, "externalServiceBasePath", BASE_URL);
    }

    @Test
    void getAllEmployees_cacheHit_returnsFromCache() {
        List<EmployeeResponse> cached = List.of(mockEmployeeResponse());
        when(cacheManager.getEmployees()).thenReturn(cached);

        List<EmployeeResponse> result = externalService.getAllEmployees();

        assertEquals(1, result.size());
        verify(restTemplate, never()).exchange(anyString(), any(), any(), ArgumentMatchers.<ParameterizedTypeReference<?>>any());
    }

    @Test
    void getAllEmployees_cacheMiss_fetchesFromExternal_andCaches() {
        when(cacheManager.getEmployees()).thenReturn(null);

        ExternalEmployeeResponseDTO externalDto = mockExternalEmployee();
        ExternalResponseDTO<List<ExternalEmployeeResponseDTO>> body =
                new ExternalResponseDTO<>(List.of(externalDto));

        ResponseEntity<ExternalResponseDTO<List<ExternalEmployeeResponseDTO>>> response =
                new ResponseEntity<>(body, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(BASE_URL),
                eq(HttpMethod.GET),
                isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<
                        ExternalResponseDTO<List<ExternalEmployeeResponseDTO>>
                        >>any()
        )).thenReturn(response);

        List<EmployeeResponse> result = externalService.getAllEmployees();

        assertEquals(1, result.size());
        verify(cacheManager).setEmployees(anyList());
    }

    @Test
    void getAllEmployees_429_throwsTooManyRequestsException() {
        when(cacheManager.getEmployees()).thenReturn(null);

        when(restTemplate.exchange(anyString(), any(), any(), ArgumentMatchers.<ParameterizedTypeReference<?>>any()))
                .thenThrow(HttpClientErrorException.TooManyRequests.create(
                        HttpStatus.TOO_MANY_REQUESTS, "429", HttpHeaders.EMPTY, null, null));

        assertThrows(TooManyRequestsException.class, () -> externalService.getAllEmployees());
    }

    @Test
    void getEmployeeById_success() {
        ExternalEmployeeResponseDTO dto = mockExternalEmployee();
        ExternalResponseDTO<ExternalEmployeeResponseDTO> body =
                new ExternalResponseDTO<>(dto);

        ResponseEntity<ExternalResponseDTO<ExternalEmployeeResponseDTO>> response =
                new ResponseEntity<>(body, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(BASE_URL + "/1"),
                eq(HttpMethod.GET),
                isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<
                        ExternalResponseDTO<ExternalEmployeeResponseDTO>
                        >>any()
        )).thenReturn(response);


        EmployeeResponse result = externalService.getEmployeeById("1");

        assertEquals("John", result.getEmployeeName());
    }

    @Test
    void getEmployeeById_notFound_throwsBadRequestException() {
        when(restTemplate.exchange(anyString(), any(), any(), ArgumentMatchers.<ParameterizedTypeReference<?>>any()))
                .thenThrow(HttpClientErrorException.NotFound.create(
                        HttpStatus.NOT_FOUND, "404", HttpHeaders.EMPTY, null, null));

        assertThrows(BadRequestException.class, () -> externalService.getEmployeeById("99"));
    }

    @Test
    void getEmployeeById_429_throwsTooManyRequestsException() {
        when(restTemplate.exchange(anyString(), any(), any(), ArgumentMatchers.<ParameterizedTypeReference<?>>any()))
                .thenThrow(HttpClientErrorException.TooManyRequests.create(
                        HttpStatus.TOO_MANY_REQUESTS, "429", HttpHeaders.EMPTY, null, null));

        assertThrows(TooManyRequestsException.class, () -> externalService.getEmployeeById("1"));
    }

    @Test
    void createEmployee_success_invalidatesCache() {
        EmployeeRequest request = new EmployeeRequest();
        ExternalEmployeeResponseDTO dto = mockExternalEmployee();

        ExternalResponseDTO<ExternalEmployeeResponseDTO> body =
                new ExternalResponseDTO<>(dto);

        ResponseEntity<ExternalResponseDTO<ExternalEmployeeResponseDTO>> response =
                new ResponseEntity<>(body, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(BASE_URL),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                ArgumentMatchers.<ParameterizedTypeReference<
                        ExternalResponseDTO<ExternalEmployeeResponseDTO>
                        >>any()
        )).thenReturn(response);


        EmployeeResponse result = externalService.createEmployee(request);

        assertNotNull(result);
        verify(cacheManager).invalidateCache();
    }

    @Test
    void deleteEmployee_success_invalidatesCache() {
        ExternalResponseDTO<Boolean> body = new ExternalResponseDTO<>(true);
        ResponseEntity<ExternalResponseDTO<Boolean>> response =
                new ResponseEntity<>(body, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(BASE_URL),
                eq(HttpMethod.DELETE),
                any(HttpEntity.class),
                ArgumentMatchers.<ParameterizedTypeReference<
                        ExternalResponseDTO<Boolean>
                        >>any()
        )).thenReturn(response);

        externalService.deleteEmployee("John");

        verify(cacheManager).invalidateCache();
    }

    @Test
    void deleteEmployee_invalidResponse_throwsBadRequestException() {
        ExternalResponseDTO<Boolean> body = new ExternalResponseDTO<>(false);
        ResponseEntity<ExternalResponseDTO<Boolean>> response =
                new ResponseEntity<>(body, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(BASE_URL),
                eq(HttpMethod.DELETE),
                any(HttpEntity.class),
                ArgumentMatchers.<ParameterizedTypeReference<
                        ExternalResponseDTO<Boolean>
                        >>any()
        )).thenReturn(response);

        assertThrows(BadRequestException.class, () -> externalService.deleteEmployee("John"));
    }

    private ExternalEmployeeResponseDTO mockExternalEmployee() {
        ExternalEmployeeResponseDTO dto = new ExternalEmployeeResponseDTO();
        dto.setId("1");
        dto.setEmployeeName("John");
        dto.setEmployeeAge(30);
        dto.setEmployeeSalary(100000);
        dto.setEmployeeTitle("Engineer");
        dto.setEmployeeEmail("john@test.com");
        return dto;
    }

    private EmployeeResponse mockEmployeeResponse() {
        return EmployeeResponse.builder()
                .id("1")
                .employeeName("John")
                .employeeSalary(100000)
                .build();
    }
}
