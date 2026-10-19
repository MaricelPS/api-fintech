package com.utn.fintech.exception;

public class CuentaNoEncontradaException extends RuntimeException {
    public CuentaNoEncontradaException(Long id) {
        super("No existe la cuenta con id " + id);
    }
}
