package com.utn.fintech.mapper;

import com.utn.fintech.dto.CuentaResponse;
import com.utn.fintech.model.Cuenta;

public final class CuentaMapper {

    private CuentaMapper() {
        // Clase utilitaria: no se instancia
    }

    public static CuentaResponse toResponse(Cuenta cuenta) {
        return new CuentaResponse(
                cuenta.getId(),
                cuenta.getNumero(),
                cuenta.getTipo().name(),
                cuenta.getSaldo(),
                cuenta.getUsuario().getId()
        );
    }
}
