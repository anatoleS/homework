package ru.otus.numbers.client;

import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.numbers.Number;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class NumbersClientObserver implements StreamObserver<Number> {
    private static final Logger log = LoggerFactory.getLogger(NumbersClientObserver.class);
    private final Object lock = new Object();
    private int lastServerValue = 0;
    private boolean serverValueUsed = true;

    public NumbersClientObserver() {
    }

    @Override
    public void onNext(Number value) {
        int v = value.getValue();
        synchronized (lock) {
            lastServerValue = v;
            serverValueUsed = false;
            lock.notifyAll();
        }
        log.info("new value:{}", v);
        System.out.println(java.time.LocalTime.now() + " new value: " + v);
    }

    public Integer takeIfNew() {
        synchronized (lock) {
            if (!serverValueUsed) {
                serverValueUsed = true;
                return lastServerValue;
            }
            return null;
        }
    }

    @Override
    public void onError(Throwable t) {
        log.error("Stream error", t);
    }

    @Override
    public void onCompleted() {
        log.info("Stream completed");
    }
}
