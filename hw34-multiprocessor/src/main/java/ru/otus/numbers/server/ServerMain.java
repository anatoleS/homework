package ru.otus.numbers.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;

public class ServerMain {
    private static final int PORT = 8190;

    public static void main(String[] args) throws IOException, InterruptedException {
        NumbersServiceImpl service = new NumbersServiceImpl();
        Server server = ServerBuilder.forPort(PORT)
                .addService(service)
                .build();
        server.start();
        System.out.println("Server started on " + PORT);
        server.awaitTermination();
    }
}
