package utils;

import java.io.IOException;
import java.io.PipedOutputStream;

public class InputSimulator implements Runnable {
    private final PipedOutputStream outputStream;
    private final String[] inputs;

    public InputSimulator(PipedOutputStream outputStream, String[] inputs) {
        this.outputStream = outputStream;
        this.inputs = inputs;
    }

    @Override
    public void run() {
        try {
            for (String input : inputs) {
                outputStream.write((input + "\n").getBytes()); // Передаем данные с символом новой строки
                outputStream.flush();
                Thread.sleep(1000); // Небольшая задержка для эмуляции реального ввода
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}