package ru.otus.numbers.client;

import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.numbers.Number;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class NumbersClientObserver implements StreamObserver<Number> {
    private static final Logger log = LoggerFactory.getLogger(NumbersClientObserver.class);
    private final AtomicInteger lastServerValue;
    private final AtomicBoolean serverValueUsed;

    public NumbersClientObserver(AtomicInteger lastServerValue, AtomicBoolean serverValueUsed) {
        this.lastServerValue = lastServerValue;
        this.serverValueUsed = serverValueUsed;
    }

    @Override
    public void onNext(Number value) {
        int v = value.getValue();
        lastServerValue.set(v);
        serverValueUsed.set(false);
        log.info("new value:{}", v);
        System.out.println(java.time.LocalTime.now() + " new value: " + v);
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
