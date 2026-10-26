package ru.otus.anatoly;

import java.util.Random;

/**
 * Задача печати чисел для одного потока.
 * Каждый поток имеет независимую последовательность 1..10..1
 * и печатает строго по очереди через SyncManager.
 */
public class PrinterTask implements Runnable {

    private final int id;
    private final SyncManager syncManager;
    private int number = 1;

    private int direction = 1;
    private static final int MIN = 1;
    private static final int MAX = 10;
    private final Random random = new Random();

    /**
     * @param id идентификатор потока 1 или 2
     * @param syncManager координатор очереди
     */
    public PrinterTask(int id, SyncManager syncManager) {
        this.id = id;
        this.syncManager = syncManager;
    }

    @Override
    public void run() {
        try {
            while (true) {
                syncManager.awaitTurn(id);
                try {
                    System.out.println("Поток " + id + ": " + number);
                    Thread.sleep(100 + random.nextInt(401));
                    updateNumber();
                } finally {
                    syncManager.nextTurn();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Обновляет число для следующей итерации с отражением на границах 1 и 10.
     */
    private void updateNumber() {
        number += direction;
        if (number >= MAX) {
            number = MAX;
            direction = -1;
        } else if (number <= MIN) {
            number = MIN;
            direction = 1;
        }
    }
}
