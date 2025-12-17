package com.reliaquest.api.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponseDTO {

    String path;

    String errorMessage;

    LocalDateTime timestamp;
}
