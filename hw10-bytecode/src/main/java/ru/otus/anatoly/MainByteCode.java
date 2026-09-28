package ru.otus.anatoly;

public class MainByteCode {
    public static void main(String[] args) {
        System.out.println("hw10-bytecode started");
        
        TestLogging test = new TestLogging();
        test.calculation(1);
        test.calculation(1, 2);
        test.calculation(1, 2, "test");
    }
}
