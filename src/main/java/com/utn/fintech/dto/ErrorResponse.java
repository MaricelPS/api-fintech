package com.utn.fintech.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
        int status,
        String mensaje,
        List<String> detalles,
        LocalDateTime timestamp
) {}
