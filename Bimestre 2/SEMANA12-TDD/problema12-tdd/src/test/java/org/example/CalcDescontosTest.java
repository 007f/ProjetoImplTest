package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalcDescontosTest {

    @Test
    void comprasAbaixoDe100SemDesconto() {
        CalcDescontos calc = new CalcDescontos();
        double desconto = calc.calcular(50.0);
    assertEquals(0.0, desconto, 0.01);
    }

    @Test
    void comprasEntre100e500Com5desconto() {
        CalcDescontos calc = new CalcDescontos();
        double desconto = calc.calcular(200.0);
    assertEquals(10.0, desconto, 0.01);
    }

    @Test
    void comprasAcimaDe500Com10desconto() {
        CalcDescontos calc = new CalcDescontos();
        double desconto = calc.calcular(600.0);
    assertEquals(60.0, desconto, 0.01);
    }

    @Test
    void valorNegativoLancaIllegalArgumentException() {
        CalcDescontos calc = new CalcDescontos();
        assertThrows(IllegalArgumentException.class, () -> calc.calcular(-50.0));
    }
}
