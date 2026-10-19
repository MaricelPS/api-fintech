package com.utn.fintech.exception;

import java.math.BigDecimal;

public class SaldoInsuficienteException extends RuntimeException {
    public SaldoInsuficienteException(Long cuentaId, BigDecimal monto) {
        super("Saldo insuficiente en la cuenta " + cuentaId + " para extraer " + monto);
    }
}
