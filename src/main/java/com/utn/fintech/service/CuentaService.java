package com.utn.fintech.service;

import com.utn.fintech.dto.CuentaRequest;
import com.utn.fintech.dto.CuentaResponse;

import java.math.BigDecimal;
import java.util.List;

public interface CuentaService {

    List<CuentaResponse> listar();

    CuentaResponse buscarPorId(Long id);

    CuentaResponse crear(CuentaRequest request);

    CuentaResponse actualizar(Long id, CuentaRequest request);

    void eliminar(Long id);

    CuentaResponse depositar(Long id, BigDecimal monto);

    CuentaResponse extraer(Long id, BigDecimal monto);
}
