package com.utn.fintech.controller;

import com.utn.fintech.dto.CuentaRequest;
import com.utn.fintech.dto.CuentaResponse;
import com.utn.fintech.dto.OperacionRequest;
import com.utn.fintech.service.CuentaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/cuentas")
public class CuentaController {

    private final CuentaService cuentaService;

    public CuentaController(CuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }

    @GetMapping
    public List<CuentaResponse> listar() {
        return cuentaService.listar();
    }

    @GetMapping("/{id}")
    public CuentaResponse buscarPorId(@PathVariable Long id) {
        return cuentaService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<CuentaResponse> crear(@Valid @RequestBody CuentaRequest request) {
        CuentaResponse creada = cuentaService.crear(request);
        return ResponseEntity
                .created(URI.create("/cuentas/" + creada.id()))
                .body(creada);
    }

    @PutMapping("/{id}")
    public CuentaResponse actualizar(@PathVariable Long id,
                                     @Valid @RequestBody CuentaRequest request) {
        return cuentaService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        cuentaService.eliminar(id);
    }

    @PostMapping("/{id}/deposito")
    public CuentaResponse depositar(@PathVariable Long id,
                                    @Valid @RequestBody OperacionRequest request) {
        return cuentaService.depositar(id, request.monto());
    }

    @PostMapping("/{id}/extraccion")
    public CuentaResponse extraer(@PathVariable Long id,
                                  @Valid @RequestBody OperacionRequest request) {
        return cuentaService.extraer(id, request.monto());
    }
}