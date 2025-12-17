package com.reliaquest.api.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmployeeRequest {

    @NotBlank(message = "Please enter valid name")
    private String name;

    @Min(value = 0, message = "Salary should be greater than 0")
    private Integer salary;

    @Min(value = 16, message = "Minimum value of the age should be 16")
    @Max(value = 75, message = "Maximum value of the age should be 75")
    private Integer age;

    @NotBlank(message = "Please enter valid title")
    private String title;

}
