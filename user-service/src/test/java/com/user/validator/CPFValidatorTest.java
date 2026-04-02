package com.user.validator;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class CPFValidatorTest {

    private final CPFValidator cpfValidator = new CPFValidator();
    private final ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

    @Test
    void isValid_DeveRetornarFalse_QuandoCpfForNulo() {
        boolean response = cpfValidator.isValid(null, context);

        assertFalse(response);
    }

    @Test
    void isValid_DeveRetornarFalse_QuandoCpfForVazio() {
        boolean response = cpfValidator.isValid("", context);

        assertFalse(response);
    }

    @Test
    void isValid_DeveRetornarFalse_QuandoCpfForBlank() {
        boolean response = cpfValidator.isValid("   ", context);

        assertFalse(response);
    }

    @Test
    void isValid_DeveRetornarFalse_QuandoCpfTiverTamanhoInvalido() {
        boolean response = cpfValidator.isValid("1234567890", context);

        assertFalse(response);
    }

    @Test
    void isValid_DeveRetornarFalse_QuandoCpfPossuirTodosDigitosIguais() {
        boolean response = cpfValidator.isValid("11111111111", context);

        assertFalse(response);
    }

    @Test
    void isValid_DeveRetornarTrue_QuandoCpfForValidoSemMascara() {
        boolean response = cpfValidator.isValid("52998224725", context);

        assertTrue(response);
    }

    @Test
    void isValid_DeveRetornarTrue_QuandoCpfForValidoComMascara() {
        boolean response = cpfValidator.isValid("529.982.247-25", context);

        assertTrue(response);
    }

    @Test
    void isValid_DeveRetornarFalse_QuandoPrimeiroDigitoVerificadorForInvalido() {
        boolean response = cpfValidator.isValid("52998224735", context);

        assertFalse(response);
    }

    @Test
    void isValid_DeveRetornarFalse_QuandoSegundoDigitoVerificadorForInvalido() {
        boolean response = cpfValidator.isValid("52998224724", context);

        assertFalse(response);
    }

    @Test
    void isValid_DeveRetornarFalse_QuandoCpfPossuirCaracteresInvalidosEAposLimpezaFicarInvalido() {
        boolean response = cpfValidator.isValid("abc", context);

        assertFalse(response);
    }
}