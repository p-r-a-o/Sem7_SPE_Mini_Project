package com.example.calculator;

import java.math.BigDecimal;
import java.math.MathContext;

import org.springframework.stereotype.Service;

@Service
public class CalculatorService {

    public BigDecimal add(BigDecimal a, BigDecimal b) {
        return a.add(b);
    }

    public BigDecimal subtract(BigDecimal a, BigDecimal b) {
        return a.subtract(b);
    }

    public BigDecimal multiply(BigDecimal a, BigDecimal b) {
        return a.multiply(b);
    }

    /**
     * Divides a by b using 16 digits of precision so non-terminating results
     * (e.g. 1/3) do not throw.
     *
     * @throws DivisionByZeroException if b is zero
     */
    public BigDecimal divide(BigDecimal a, BigDecimal b) {
        if (b.signum() == 0) {
            throw new DivisionByZeroException("Division by zero is not allowed");
        }
        return a.divide(b, MathContext.DECIMAL64);
    }
}
