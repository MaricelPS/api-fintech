package com.utn.fintech.model;

import com.utn.fintech.exception.SaldoInsuficienteException;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue("CUENTA_CORRIENTE")
public class CuentaCorriente extends Cuenta {

    @Column(name = "limite_descubierto")
    private BigDecimal limiteDescubierto = BigDecimal.ZERO;

    // JPA exige un constructor sin argumentos
    public CuentaCorriente() {
    }

    public CuentaCorriente(BigDecimal limiteDescubierto) {
        this.limiteDescubierto = limiteDescubierto != null ? limiteDescubierto : BigDecimal.ZERO;
    }

    @Override
    public void extraer(BigDecimal monto) {
        validarMontoPositivo(monto);
        BigDecimal disponible = saldo.add(limiteDescubierto);
        if (disponible.compareTo(monto) < 0) {
            throw new SaldoInsuficienteException(getId(), monto);
        }
        saldo = saldo.subtract(monto);
    }

    public BigDecimal getLimiteDescubierto() { return limiteDescubierto; }
    public void setLimiteDescubierto(BigDecimal limiteDescubierto) { this.limiteDescubierto = limiteDescubierto; }
}
