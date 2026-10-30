package ru.otus.numbers.server;

import io.grpc.stub.StreamObserver;
import ru.otus.numbers.Number;
import ru.otus.numbers.NumbersServiceGrpc;
import ru.otus.numbers.Request;

public class NumbersServiceImpl extends NumbersServiceGrpc.NumbersServiceImplBase {

    @Override
    public void generateNumbers(Request request, StreamObserver<Number> responseObserver) {
        int firstValue = request.getFirstValue();
        int lastValue = request.getLastValue();

        for (int i = firstValue + 1; i <= lastValue; i++) {
            Number number = Number.newBuilder()
                    .setValue(i)
                    .build();
            responseObserver.onNext(number);
            System.out.println(java.time.LocalTime.now() + " server sent: " + i);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                responseObserver.onError(e);
                return;
            }
        }
        responseObserver.onCompleted();
    }
}
