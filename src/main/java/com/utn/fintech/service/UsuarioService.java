package com.utn.fintech.service;

import com.utn.fintech.dto.UsuarioRequest;
import com.utn.fintech.dto.UsuarioResponse;

import java.util.List;

public interface UsuarioService {

    List<UsuarioResponse> listar();

    UsuarioResponse buscarPorId(Long id);

    UsuarioResponse crear(UsuarioRequest request);
}
