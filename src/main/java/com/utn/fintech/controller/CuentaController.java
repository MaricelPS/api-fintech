package com.utn.fintech.controller;

import com.utn.fintech.dto.CuentaRequest;
import com.utn.fintech.dto.CuentaResponse;
import com.utn.fintech.dto.ErrorResponse;
import com.utn.fintech.dto.OperacionRequest;
import com.utn.fintech.service.CuentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/cuentas")
@Tag(name = "Cuentas", description = "Alta, consulta, modificación, baja y operaciones de cuentas")
public class CuentaController {

    private final CuentaService cuentaService;

    public CuentaController(CuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }

    @Operation(summary = "Listar cuentas", description = "Devuelve todas las cuentas registradas.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    @GetMapping
    public List<CuentaResponse> listar() {
        return cuentaService.listar();
    }

    @Operation(summary = "Obtener una cuenta por ID")
    @ApiResponse(responseCode = "200", description = "Cuenta encontrada")
    @ApiResponse(responseCode = "404", description = "La cuenta no existe",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}")
    public CuentaResponse buscarPorId(
            @Parameter(description = "ID de la cuenta", example = "1") @PathVariable Long id) {
        return cuentaService.buscarPorId(id);
    }

    @Operation(summary = "Crear una cuenta",
            description = "Crea una caja de ahorro o una cuenta corriente asociada a un usuario existente. " +
                    "El límite de descubierto solo aplica a cuentas corrientes.")
    @ApiResponse(responseCode = "201", description = "Cuenta creada")
    @ApiResponse(responseCode = "400", description = "Datos inválidos",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "El usuario no existe",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Ya existe una cuenta con ese número",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    public ResponseEntity<CuentaResponse> crear(@Valid @RequestBody CuentaRequest request) {
        CuentaResponse creada = cuentaService.crear(request);
        return ResponseEntity
                .created(URI.create("/cuentas/" + creada.id()))
                .body(creada);
    }

    @Operation(summary = "Actualizar una cuenta",
            description = "Permite modificar el número y, en cuentas corrientes, el límite de descubierto. " +
                    "No se puede cambiar el tipo de cuenta ni su titular.")
    @ApiResponse(responseCode = "200", description = "Cuenta actualizada")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o intento de cambiar el tipo",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "La cuenta no existe",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Ya existe otra cuenta con ese número",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PutMapping("/{id}")
    public CuentaResponse actualizar(
            @Parameter(description = "ID de la cuenta", example = "1") @PathVariable Long id,
            @Valid @RequestBody CuentaRequest request) {
        return cuentaService.actualizar(id, request);
    }

    @Operation(summary = "Eliminar una cuenta")
    @ApiResponse(responseCode = "204", description = "Cuenta eliminada")
    @ApiResponse(responseCode = "404", description = "La cuenta no existe",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(
            @Parameter(description = "ID de la cuenta", example = "1") @PathVariable Long id) {
        cuentaService.eliminar(id);
    }

    @Operation(summary = "Depositar dinero", description = "Suma el monto indicado al saldo de la cuenta.")
    @ApiResponse(responseCode = "200", description = "Depósito realizado")
    @ApiResponse(responseCode = "400", description = "Monto inválido",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "La cuenta no existe",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/{id}/deposito")
    public CuentaResponse depositar(
            @Parameter(description = "ID de la cuenta", example = "1") @PathVariable Long id,
            @Valid @RequestBody OperacionRequest request) {
        return cuentaService.depositar(id, request.monto());
    }

    @Operation(summary = "Extraer dinero",
            description = "Resta el monto del saldo. Una caja de ahorro no puede quedar en negativo; " +
                    "una cuenta corriente puede usar su límite de descubierto.")
    @ApiResponse(responseCode = "200", description = "Extracción realizada")
    @ApiResponse(responseCode = "400", description = "Monto inválido",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "La cuenta no existe",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "422", description = "Saldo insuficiente",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/{id}/extraccion")
    public CuentaResponse extraer(
            @Parameter(description = "ID de la cuenta", example = "1") @PathVariable Long id,
            @Valid @RequestBody OperacionRequest request) {
        return cuentaService.extraer(id, request.monto());
    }
}