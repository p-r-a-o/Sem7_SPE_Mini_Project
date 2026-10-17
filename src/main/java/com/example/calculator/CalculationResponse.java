package com.example.calculator;

import java.math.BigDecimal;

public record CalculationResponse(String operation, BigDecimal a, BigDecimal b, BigDecimal result) {
}
