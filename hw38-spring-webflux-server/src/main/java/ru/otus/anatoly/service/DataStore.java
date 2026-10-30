package ru.otus.anatoly.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.anatoly.domain.Message;

public interface DataStore {
    Mono<Message> saveMessage(Message message);
    Flux<Message> loadMessages(String roomId);
    Flux<Message> loadAggregatedMessages();
}
