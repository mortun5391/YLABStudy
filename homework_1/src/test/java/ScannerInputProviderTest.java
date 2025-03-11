

import com.financetracker.service.ScannerInputProvider;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScannerInputProviderTest {

    @Test
    void testNextInt() {
        String input = "42";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ScannerInputProvider inputProvider = new ScannerInputProvider(new Scanner(System.in));

        int result = inputProvider.nextInt();
        assertEquals(42, result);

        System.setIn(System.in);
    }

    @Test
    void testNextDouble() {
        String input = "3,14";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ScannerInputProvider inputProvider = new ScannerInputProvider(new Scanner(System.in));

        double result = inputProvider.nextDouble();
        assertEquals(3.14, result, 0.001);

        System.setIn(System.in);
    }

    @Test
    void testNextLine() {
        String input = "Hello, World!";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ScannerInputProvider inputProvider = new ScannerInputProvider(new Scanner(System.in));

        String result = inputProvider.nextLine();
        assertEquals("Hello, World!", result);

        System.setIn(System.in);
    }
}