package com.utn.fintech.exception;

import com.utn.fintech.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 404: el recurso pedido no existe
    @ExceptionHandler({CuentaNoEncontradaException.class, UsuarioNoEncontradoException.class})
    public ResponseEntity<ErrorResponse> handleNoEncontrado(RuntimeException ex) {
        return construir(HttpStatus.NOT_FOUND, ex.getMessage(), List.of());
    }

    // 409: conflicto con un dato que ya existe
    @ExceptionHandler(RecursoDuplicadoException.class)
    public ResponseEntity<ErrorResponse> handleDuplicado(RecursoDuplicadoException ex) {
        return construir(HttpStatus.CONFLICT, ex.getMessage(), List.of());
    }

    // 422: la petición es válida, pero viola una regla de negocio
    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<ErrorResponse> handleSaldoInsuficiente(SaldoInsuficienteException ex) {
        return construir(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage(), List.of());
    }

    // 400: datos inválidos en el body (anotaciones de los DTOs)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidacion(MethodArgumentNotValidException ex) {
        List<String> detalles = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        return construir(HttpStatus.BAD_REQUEST, "Datos inválidos", detalles);
    }

    // 400: argumentos inválidos detectados en el dominio (ej. monto negativo)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleArgumentoInvalido(IllegalArgumentException ex) {
        return construir(HttpStatus.BAD_REQUEST, ex.getMessage(), List.of());
    }

    // 404: la ruta pedida no existe
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleRutaInexistente(NoResourceFoundException ex) {
        return construir(HttpStatus.NOT_FOUND, "La ruta solicitada no existe", List.of());
    }

    // 400: body ausente o JSON mal formado
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleBodyInvalido(HttpMessageNotReadableException ex) {
        return construir(HttpStatus.BAD_REQUEST, "El cuerpo de la petición falta o no es un JSON válido", List.of());
    }

    // 500: cualquier otro error no previsto
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenerico(Exception ex) {
        log.error("Error no controlado", ex); // queda registrado en la consola con todo el detalle
        return construir(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", List.of());
    }

    private ResponseEntity<ErrorResponse> construir(HttpStatus status, String mensaje, List<String> detalles) {
        ErrorResponse body = new ErrorResponse(status.value(), mensaje, detalles, LocalDateTime.now());
        return ResponseEntity.status(status).body(body);
    }
}
