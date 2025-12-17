package com.reliaquest.api.external.dto;

import lombok.Data;

@Data
public class ExternalResponseDTO<T> {

    T data;

    String status;
}


