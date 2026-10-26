package ru.otus.anatoly;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainExecutors {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        SyncManager syncManager = new SyncManager();
        executor.submit(new PrinterTask(1, syncManager));
        executor.submit(new PrinterTask(2, syncManager));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executor.shutdownNow();
        }));
    }
}
