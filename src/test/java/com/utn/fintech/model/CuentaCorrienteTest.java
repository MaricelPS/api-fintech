package com.utn.fintech.model;

import com.utn.fintech.exception.SaldoInsuficienteException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CuentaCorrienteTest {

    @Test
    @DisplayName("Puede extraer usando el descubierto y quedar en negativo")
    void extraer_dentroDelDescubierto_quedaEnNegativo() {
        CuentaCorriente cuenta = new CuentaCorriente(new BigDecimal("5000"));
        cuenta.depositar(new BigDecimal("1000"));

        cuenta.extraer(new BigDecimal("3000"));

        assertEquals(0, new BigDecimal("-2000").compareTo(cuenta.getSaldo()));
    }

    @Test
    @DisplayName("Extraer más que saldo + descubierto lanza excepción")
    void extraer_superaDescubierto_lanzaExcepcion() {
        CuentaCorriente cuenta = new CuentaCorriente(new BigDecimal("5000"));
        cuenta.depositar(new BigDecimal("1000"));

        assertThrows(SaldoInsuficienteException.class,
                () -> cuenta.extraer(new BigDecimal("6001")));
    }

    @Test
    @DisplayName("Sin límite informado, el descubierto es cero")
    void constructor_limiteNulo_descubiertoCero() {
        CuentaCorriente cuenta = new CuentaCorriente(null);

        assertEquals(0, BigDecimal.ZERO.compareTo(cuenta.getLimiteDescubierto()));
        assertThrows(SaldoInsuficienteException.class,
                () -> cuenta.extraer(new BigDecimal("1")));
    }

    @Test
    @DisplayName("El tipo de una cuenta corriente es CUENTA_CORRIENTE")
    void getTipo_devuelveCuentaCorriente() {
        assertEquals(TipoCuenta.CUENTA_CORRIENTE, new CuentaCorriente().getTipo());
    }
}
