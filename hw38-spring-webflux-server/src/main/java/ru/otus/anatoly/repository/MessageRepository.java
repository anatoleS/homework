package ru.otus.anatoly.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import ru.otus.anatoly.domain.Message;

public interface MessageRepository extends ReactiveCrudRepository<Message, Long> {

    @Query("select * from message where room_id = :roomId order by id")
    Flux<Message> findByRoomId(@Param("roomId") String roomId);

    Flux<Message> findTop5ByOrderByIdDesc();

    @Query("select * from message order by id")
    Flux<Message> findAllOrdered();
}
