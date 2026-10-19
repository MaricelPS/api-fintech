package com.utn.fintech.dto;

import java.math.BigDecimal;

public record CuentaResponse(
        Long id,
        String numero,
        String tipo,
        BigDecimal saldo,
        Long usuarioId
) {}
