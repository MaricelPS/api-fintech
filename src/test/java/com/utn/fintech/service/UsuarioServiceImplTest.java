package com.utn.fintech.service;

import com.utn.fintech.dto.UsuarioRequest;
import com.utn.fintech.dto.UsuarioResponse;
import com.utn.fintech.exception.RecursoDuplicadoException;
import com.utn.fintech.exception.UsuarioNoEncontradoException;
import com.utn.fintech.model.Usuario;
import com.utn.fintech.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioServiceImpl service;

    private final UsuarioRequest request =
            new UsuarioRequest("Ana Gómez", "ana@mail.com", "30123456");

    @Test
    @DisplayName("crear un usuario válido lo guarda y devuelve sus datos")
    void crear_datosValidos_guardaUsuario() {
        when(usuarioRepository.existsByEmail("ana@mail.com")).thenReturn(false);
        when(usuarioRepository.existsByDni("30123456")).thenReturn(false);
        when(usuarioRepository.saveAndFlush(any(Usuario.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        UsuarioResponse response = service.crear(request);

        assertEquals("Ana Gómez", response.nombre());
        assertEquals("ana@mail.com", response.email());
        assertTrue(response.cuentas().isEmpty());
    }

    @Test
    @DisplayName("crear con email duplicado lanza excepción y no guarda")
    void crear_emailDuplicado_lanzaExcepcion() {
        when(usuarioRepository.existsByEmail("ana@mail.com")).thenReturn(true);

        assertThrows(RecursoDuplicadoException.class, () -> service.crear(request));
        verify(usuarioRepository, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("crear con DNI duplicado lanza excepción y no guarda")
    void crear_dniDuplicado_lanzaExcepcion() {
        when(usuarioRepository.existsByEmail("ana@mail.com")).thenReturn(false);
        when(usuarioRepository.existsByDni("30123456")).thenReturn(true);

        assertThrows(RecursoDuplicadoException.class, () -> service.crear(request));
        verify(usuarioRepository, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("buscarPorId lanza excepción cuando el usuario no existe")
    void buscarPorId_inexistente_lanzaExcepcion() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNoEncontradoException.class, () -> service.buscarPorId(99L));
    }
}
