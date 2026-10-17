package com.example.calculator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class CalculatorServiceTest {

    private final CalculatorService service = new CalculatorService();

    private static BigDecimal bd(String v) {
        return new BigDecimal(v);
    }

    @Test
    void addReturnsSum() {
        assertEquals(0, bd("5").compareTo(service.add(bd("2"), bd("3"))));
        assertEquals(0, bd("0.3").compareTo(service.add(bd("0.1"), bd("0.2"))));
    }

    @Test
    void subtractReturnsDifference() {
        assertEquals(0, bd("-1").compareTo(service.subtract(bd("2"), bd("3"))));
    }

    @Test
    void multiplyReturnsProduct() {
        assertEquals(0, bd("6").compareTo(service.multiply(bd("2"), bd("3"))));
        assertEquals(0, bd("0").compareTo(service.multiply(bd("0"), bd("99"))));
    }

    @Test
    void divideReturnsQuotient() {
        assertEquals(0, bd("2.5").compareTo(service.divide(bd("10"), bd("4"))));
    }

    @Test
    void divideHandlesNonTerminatingResult() {
        assertEquals(0, bd("0.3333333333333333").compareTo(service.divide(bd("1"), bd("3"))));
    }

    @Test
    void divideByZeroThrows() {
        assertThrows(DivisionByZeroException.class, () -> service.divide(bd("1"), bd("0")));
        assertThrows(DivisionByZeroException.class, () -> service.divide(bd("1"), bd("0.0")));
    }
}
