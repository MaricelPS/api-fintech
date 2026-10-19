package com.utn.fintech.mapper;

import com.utn.fintech.dto.UsuarioResponse;
import com.utn.fintech.model.Usuario;

public final class UsuarioMapper {

    private UsuarioMapper() {
    }

    public static UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getDni(),
                usuario.getCuentas().stream()
                        .map(CuentaMapper::toResponse)
                        .toList()
        );
    }
}
