package com.utn.fintech.model;

import java.math.BigDecimal;

public interface Operable {
    void depositar(BigDecimal monto);
    void extraer(BigDecimal monto);
}
