package com.utn.fintech.model;

import com.utn.fintech.exception.SaldoInsuficienteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CajaAhorroTest {

    private CajaAhorro cuenta;

    @BeforeEach
    void setUp() {
        // Se ejecuta antes de CADA test: cada uno arranca con una cuenta nueva
        cuenta = new CajaAhorro();
    }

    @Test
    @DisplayName("Depositar un monto positivo aumenta el saldo")
    void depositar_montoPositivo_aumentaSaldo() {
        cuenta.depositar(new BigDecimal("1000"));

        assertSaldo("1000");
    }

    @Test
    @DisplayName("Depositar un monto negativo lanza excepción")
    void depositar_montoNegativo_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> cuenta.depositar(new BigDecimal("-50")));
    }

    @Test
    @DisplayName("Depositar cero lanza excepción")
    void depositar_montoCero_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> cuenta.depositar(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Extraer con saldo suficiente descuenta el monto")
    void extraer_conSaldoSuficiente_descuentaMonto() {
        cuenta.depositar(new BigDecimal("1000"));

        cuenta.extraer(new BigDecimal("400"));

        assertSaldo("600");
    }

    @Test
    @DisplayName("Extraer exactamente el saldo deja la cuenta en cero")
    void extraer_saldoExacto_quedaEnCero() {
        cuenta.depositar(new BigDecimal("500"));

        cuenta.extraer(new BigDecimal("500"));

        assertSaldo("0");
    }

    @Test
    @DisplayName("Extraer más que el saldo lanza excepción y no modifica el saldo")
    void extraer_sinSaldoSuficiente_lanzaExcepcionYNoModificaSaldo() {
        cuenta.depositar(new BigDecimal("100"));

        assertThrows(SaldoInsuficienteException.class,
                () -> cuenta.extraer(new BigDecimal("101")));
        assertSaldo("100");
    }

    @Test
    @DisplayName("El tipo de una caja de ahorro es CAJA_AHORRO")
    void getTipo_devuelveCajaAhorro() {
        assertEquals(TipoCuenta.CAJA_AHORRO, cuenta.getTipo());
    }

    // Con BigDecimal se compara con compareTo: equals considera distintos 600 y 600.00
    private void assertSaldo(String esperado) {
        assertEquals(0, new BigDecimal(esperado).compareTo(cuenta.getSaldo()),
                "Saldo esperado " + esperado + " pero fue " + cuenta.getSaldo());
    }
}
