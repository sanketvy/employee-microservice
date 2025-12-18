package com.reliaquest.api.external.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExternalResponseDTO<T> {

    T data;

    String status;

    public ExternalResponseDTO(T data){
        this.data = data;
    }
}


