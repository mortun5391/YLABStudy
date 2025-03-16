package utils;

import com.financetracker.utils.InputProvider;
import com.financetracker.utils.InputValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InputValidatorTest {

    private InputProvider inputProvider;
    private InputValidator inputValidator;

    @BeforeEach
    void setUp() {
        inputProvider = Mockito.mock(InputProvider.class);
        inputValidator = new InputValidator(inputProvider);
    }

    @Test
    void testGetIntInput_ValidInput() {
        when(inputProvider.nextInt()).thenReturn(42);
        when(inputProvider.nextLine()).thenReturn("");

        int result = inputValidator.getIntInput("Введите целое число: ");

        assertEquals(42, result);
        verify(inputProvider, times(1)).nextInt();
        verify(inputProvider, times(1)).nextLine();
    }

    @Test
    void testGetDoubleInput_ValidInput() {
        when(inputProvider.nextDouble()).thenReturn(3.14);
        when(inputProvider.nextLine()).thenReturn("");

        double result = inputValidator.getDoubleInput("Введите число с плавающей точкой: ");

        assertEquals(3.14, result, 0.001);
        verify(inputProvider, times(1)).nextDouble();
        verify(inputProvider, times(1)).nextLine();
    }

    @Test
    void testGetDateInput_ValidInput() {
        when(inputProvider.nextLine()).thenReturn("2023-10-01");

        LocalDate result = inputValidator.getDateInput("Введите дату (гггг-мм-дд): ");

        assertEquals(LocalDate.of(2023, 10, 1), result);
        verify(inputProvider, times(1)).nextLine();
    }

    @Test
    void testGetBooleanInput_ValidInputTrue() {
        when(inputProvider.nextLine()).thenReturn("true");

        boolean result = inputValidator.getBooleanInput("Введите true или false: ");

        assertTrue(result);
        verify(inputProvider, times(1)).nextLine();
    }

    @Test
    void testGetStringInput_ValidInput() {
        when(inputProvider.nextLine()).thenReturn("Valid String");

        String result = inputValidator.getStringInput("Введите строку: ");

        assertEquals("Valid String", result);
        verify(inputProvider, times(1)).nextLine();
    }
}