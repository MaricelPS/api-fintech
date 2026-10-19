package com.utn.fintech.dto;

import com.utn.fintech.model.TipoCuenta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record CuentaRequest(
        @NotBlank(message = "El número de cuenta es obligatorio")
        String numero,

        @NotNull(message = "El tipo de cuenta es obligatorio")
        TipoCuenta tipo,

        @NotNull(message = "El id de usuario es obligatorio")
        Long usuarioId,

        @PositiveOrZero(message = "El límite de descubierto no puede ser negativo")
        BigDecimal limiteDescubierto
) {}
