package ru.otus.numbers.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.numbers.Number;
import ru.otus.numbers.NumbersServiceGrpc;
import ru.otus.numbers.Request;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class ClientMain {
    private static final Logger log = LoggerFactory.getLogger(ClientMain.class);
    private static final String HOST = "localhost";
    private static final int PORT = 8190;

    public static void main(String[] args) throws InterruptedException {
        log.info("numbers Client is starting...");
        int firstValue = 0;
        int lastValue = 30;
        System.out.printf("args: %s%n", Arrays.toString(args));
        if (args.length >= 2) {
            try {
                firstValue = Integer.parseInt(args[0]);
                lastValue = Integer.parseInt(args[1]);
                log.info("Using custom range {} {}", firstValue, lastValue);
            } catch (NumberFormatException e) {
                log.warn("Invalid args, using defaults 0 30");
            }
        }
        ManagedChannel channel = ManagedChannelBuilder.forAddress(HOST, PORT)
                .usePlaintext()
                .build();

        NumbersServiceGrpc.NumbersServiceStub stub = NumbersServiceGrpc.newStub(channel);

        NumbersClientObserver observer = new NumbersClientObserver();

        Request request = Request.newBuilder()
                .setFirstValue(firstValue)
                .setLastValue(lastValue)
                .build();

        stub.generateNumbers(request, observer);

        int currentValue = 0;
        for (int i = 0; i <= 50; i++) {
            Thread.sleep(1000);
            Integer newServer = observer.takeIfNew();
            if (newServer != null) {
                currentValue = currentValue + newServer + 1;
            } else {
                currentValue = currentValue + 1;
            }
            log.info("currentValue:{}", currentValue);
            System.out.println(java.time.LocalTime.now() + " currentValue:" + currentValue);
        }

        channel.shutdown();
        if (!channel.awaitTermination(5, TimeUnit.SECONDS)) {
            channel.shutdownNow();
        }
    }
}
