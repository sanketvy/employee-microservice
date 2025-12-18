package com.reliaquest.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeResponse {

    private String id;

    private String employeeName;

    private Integer employeeSalary;

    private Integer employeeAge;

    private String employeeTitle;

    private String employeeEmail;
}
