package com.utn.fintech.dto;

import java.util.List;

public record UsuarioResponse(
        Long id,
        String nombre,
        String email,
        String dni,
        List<CuentaResponse> cuentas
) {}
