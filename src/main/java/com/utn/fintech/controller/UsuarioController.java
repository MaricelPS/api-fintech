package com.utn.fintech.controller;

import com.utn.fintech.dto.ErrorResponse;
import com.utn.fintech.dto.UsuarioRequest;
import com.utn.fintech.dto.UsuarioResponse;
import com.utn.fintech.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuarios", description = "Alta y consulta de usuarios titulares de cuentas")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Listar usuarios",
            description = "Devuelve todos los usuarios registrados, cada uno con sus cuentas.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    @GetMapping
    public List<UsuarioResponse> listar() {
        return usuarioService.listar();
    }

    @Operation(summary = "Obtener un usuario por ID",
            description = "Devuelve los datos del usuario y el detalle de sus cuentas.")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado")
    @ApiResponse(responseCode = "404", description = "El usuario no existe",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}")
    public UsuarioResponse buscarPorId(
            @Parameter(description = "ID del usuario", example = "1") @PathVariable Long id) {
        return usuarioService.buscarPorId(id);
    }

    @Operation(summary = "Crear un usuario",
            description = "Registra un nuevo usuario. El email y el DNI deben ser únicos.")
    @ApiResponse(responseCode = "201", description = "Usuario creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Ya existe un usuario con ese email o DNI",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody UsuarioRequest request) {
        UsuarioResponse creado = usuarioService.crear(request);
        return ResponseEntity
                .created(URI.create("/usuarios/" + creado.id()))
                .body(creado);
    }
}
