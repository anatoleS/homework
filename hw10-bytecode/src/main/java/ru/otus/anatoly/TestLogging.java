package ru.otus.anatoly;

public class TestLogging implements TestLoggingInterface {
    @Log
    @Override
    public void calculation(int param1) {
        System.out.println("calculation with 1 param: " + param1 * 1000);
    }

    @Log
    @Override
    public void calculation(int param1, int param2) {
        System.out.println("calculation with 2 params: " + param1 * 2000 + ", " + param2 * 3000);
    }

    @Log
    @Override
    public void calculation(int param1, int param2, String param3) {
        System.out.println("calculation with 3 params: " + param1 * 2 + ", " + param2 * 3 + ", " + param3);
    }
}
