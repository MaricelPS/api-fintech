package com.utn.fintech.exception;

public class UsuarioNoEncontradoException extends RuntimeException {
    public UsuarioNoEncontradoException(Long id) {
        super("No existe el usuario con id " + id);
    }
}
