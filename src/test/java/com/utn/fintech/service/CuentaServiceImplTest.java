package com.utn.fintech.service;

import com.utn.fintech.dto.CuentaRequest;
import com.utn.fintech.dto.CuentaResponse;
import com.utn.fintech.exception.CuentaNoEncontradaException;
import com.utn.fintech.exception.RecursoDuplicadoException;
import com.utn.fintech.exception.SaldoInsuficienteException;
import com.utn.fintech.exception.UsuarioNoEncontradoException;
import com.utn.fintech.model.CajaAhorro;
import com.utn.fintech.model.Cuenta;
import com.utn.fintech.model.TipoCuenta;
import com.utn.fintech.model.Usuario;
import com.utn.fintech.repository.CuentaRepository;
import com.utn.fintech.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CuentaServiceImplTest {

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CuentaServiceImpl service;

    // ---------- Datos de prueba ----------

    private CajaAhorro cajaConUsuario(String numero) {
        Usuario usuario = new Usuario();
        CajaAhorro caja = new CajaAhorro();
        caja.setNumero(numero);
        usuario.agregarCuenta(caja);
        return caja;
    }

    private CuentaRequest requestCaja(String numero) {
        return new CuentaRequest(numero, TipoCuenta.CAJA_AHORRO, 1L, null);
    }

    // ---------- buscarPorId ----------

    @Test
    @DisplayName("buscarPorId devuelve la cuenta cuando existe")
    void buscarPorId_existente_devuelveCuenta() {
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cajaConUsuario("CA-1")));

        CuentaResponse response = service.buscarPorId(1L);

        assertEquals("CA-1", response.numero());
        assertEquals("CAJA_AHORRO", response.tipo());
    }

    @Test
    @DisplayName("buscarPorId lanza excepción cuando la cuenta no existe")
    void buscarPorId_inexistente_lanzaCuentaNoEncontrada() {
        when(cuentaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CuentaNoEncontradaException.class, () -> service.buscarPorId(99L));
    }

    // ---------- crear ----------

    @Test
    @DisplayName("crear una caja de ahorro la guarda asociada al usuario")
    void crear_cajaAhorro_guardaYAsociaAlUsuario() {
        Usuario usuario = new Usuario();
        when(cuentaRepository.existsByNumero("CA-1")).thenReturn(false);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        // El "save" falso devuelve la misma cuenta que recibe
        when(cuentaRepository.saveAndFlush(any(Cuenta.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        CuentaResponse response = service.crear(requestCaja("CA-1"));

        assertEquals("CA-1", response.numero());
        assertEquals("CAJA_AHORRO", response.tipo());
        assertEquals(0, BigDecimal.ZERO.compareTo(response.saldo()));
        assertEquals(1, usuario.getCuentas().size());
        verify(cuentaRepository).saveAndFlush(any(Cuenta.class));
    }

    @Test
    @DisplayName("crear con número duplicado lanza excepción y no guarda")
    void crear_numeroDuplicado_lanzaExcepcionYNoGuarda() {
        when(cuentaRepository.existsByNumero("CA-1")).thenReturn(true);

        assertThrows(RecursoDuplicadoException.class, () -> service.crear(requestCaja("CA-1")));
        verify(cuentaRepository, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("crear con usuario inexistente lanza excepción")
    void crear_usuarioInexistente_lanzaExcepcion() {
        when(cuentaRepository.existsByNumero("CA-1")).thenReturn(false);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNoEncontradoException.class, () -> service.crear(requestCaja("CA-1")));
        verify(cuentaRepository, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("crear traduce el error de la base de datos a RecursoDuplicadoException")
    void crear_baseRechazaDuplicado_traduceExcepcion() {
        when(cuentaRepository.existsByNumero("CA-1")).thenReturn(false);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(new Usuario()));
        when(cuentaRepository.saveAndFlush(any(Cuenta.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        assertThrows(RecursoDuplicadoException.class, () -> service.crear(requestCaja("CA-1")));
    }

    // ---------- actualizar ----------

    @Test
    @DisplayName("actualizar no permite cambiar el tipo de cuenta")
    void actualizar_cambiandoTipo_lanzaExcepcion() {
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cajaConUsuario("CA-1")));
        CuentaRequest request = new CuentaRequest("CA-1", TipoCuenta.CUENTA_CORRIENTE, 1L, null);

        assertThrows(IllegalArgumentException.class, () -> service.actualizar(1L, request));
        verify(cuentaRepository, never()).saveAndFlush(any());
    }

    // ---------- eliminar ----------

    @Test
    @DisplayName("eliminar una cuenta inexistente lanza excepción y no borra nada")
    void eliminar_inexistente_lanzaExcepcion() {
        when(cuentaRepository.existsById(99L)).thenReturn(false);

        assertThrows(CuentaNoEncontradaException.class, () -> service.eliminar(99L));
        verify(cuentaRepository, never()).deleteById(any());
    }

    // ---------- depositar / extraer ----------

    @Test
    @DisplayName("depositar suma el monto y guarda la cuenta")
    void depositar_ok_actualizaSaldo() {
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cajaConUsuario("CA-1")));
        when(cuentaRepository.save(any(Cuenta.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        CuentaResponse response = service.depositar(1L, new BigDecimal("2500"));

        assertEquals(0, new BigDecimal("2500").compareTo(response.saldo()));
    }

    @Test
    @DisplayName("extraer sin saldo suficiente lanza excepción y no guarda")
    void extraer_sinSaldo_lanzaExcepcionYNoGuarda() {
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cajaConUsuario("CA-1")));

        assertThrows(SaldoInsuficienteException.class,
                () -> service.extraer(1L, new BigDecimal("100")));
        verify(cuentaRepository, never()).save(any());
    }
}
