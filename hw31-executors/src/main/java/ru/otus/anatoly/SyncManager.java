package ru.otus.anatoly;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SyncManager {
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private int currentTurn = 1;

    /**
     * Ожидает своей очереди.
     * Поток блокируется до тех пор, пока currentTurn != id.
     * @param id потока 1 или 2
     * @throws InterruptedException
     */
    public void awaitTurn(int id) throws InterruptedException {
        lock.lock();
        try {
            while (currentTurn != id) {
                condition.await();
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Переключает очередь на следующий поток и будит остальных (в наем случае другой поток)
     * Текущий ход меняется 1 <---> 2.
     */
    public void nextTurn() {
        lock.lock();
        try {
            currentTurn = 3 - currentTurn;
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
