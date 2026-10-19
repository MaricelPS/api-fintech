package com.utn.fintech.model;

import com.utn.fintech.exception.SaldoInsuficienteException;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue("CAJA_AHORRO")
public class CajaAhorro extends Cuenta {

    @Override
    public void extraer(BigDecimal monto) {
        validarMontoPositivo(monto);
        if (saldo.compareTo(monto) < 0) {
            throw new SaldoInsuficienteException(getId(), monto);
        }
        saldo = saldo.subtract(monto);
    }
}
