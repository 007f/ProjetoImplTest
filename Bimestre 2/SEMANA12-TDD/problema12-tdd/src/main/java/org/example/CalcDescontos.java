package org.example;

public class CalcDescontos {

    public double calcular(double valor) {
        if (valor < 0) {
            throw new IllegalArgumentException("Valor não pode ser negativo");
        }
        if (valor < 100.0) {
            return 0.0;
        }
        if (valor <= 500.0) {
            return round(valor * 0.05);
        }
        return round(valor * 0.10);
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
