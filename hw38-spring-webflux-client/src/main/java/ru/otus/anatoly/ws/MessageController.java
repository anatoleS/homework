package ru.otus.anatoly.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.util.HtmlUtils;
import reactor.core.publisher.Flux;
import ru.otus.anatoly.domain.Message;
import ru.otus.anatoly.domain.MessageDto;

@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    private static final String TOPIC_TEMPLATE = "/topic/response.";
    private static final String ROOM_1408 = "1408";

    private final SimpMessagingTemplate template;
    private final WebClient datastoreClient;

    public MessageController(SimpMessagingTemplate template, WebClient datastoreClient) {
        this.template = template;
        this.datastoreClient = datastoreClient;
    }

    @MessageMapping("/message.{roomId}")
    public void getMessage(@DestinationVariable("roomId") String roomId, Message message) {
        logger.info("get message:{}, roomId:{}", message, roomId);
        if (ROOM_1408.equals(roomId)) {
            logger.warn("Attempt to send message to read-only room 1408, ignoring");
            return;
        }
        saveMessage(roomId, message).subscribe(msgId -> {
            logger.info("message saved id:{}", msgId);
            var escaped = new Message(HtmlUtils.htmlEscape(message.messageStr()));
            template.convertAndSend(TOPIC_TEMPLATE + roomId, escaped);
            template.convertAndSend(TOPIC_TEMPLATE + ROOM_1408, escaped);
        });
    }

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        var genericMessage = (GenericMessage<byte[]>) event.getMessage();
        var simpDestination = (String) genericMessage.getHeaders().get("simpDestination");
        if (simpDestination == null || !simpDestination.startsWith(TOPIC_TEMPLATE)) {
            return;
        }
        var roomId = parseRoomId(simpDestination);
        var principal = event.getUser();
        if (principal == null) {
            return;
        }
        logger.info("subscription for:{}, roomId:{}, user:{}", simpDestination, roomId, principal.getName());

        Flux<Message> history = getMessagesByRoomId(roomId);
        history.subscribe(msg -> template.convertAndSendToUser(principal.getName(), simpDestination, msg));
    }

    private String parseRoomId(String simpDestination) {
        try {
            var idx = simpDestination.lastIndexOf(TOPIC_TEMPLATE);
            return simpDestination.substring(idx + TOPIC_TEMPLATE.length());
        } catch (Exception ex) {
            logger.error("Can not get roomId", ex);
            return "";
        }
    }

    private reactor.core.publisher.Mono<Long> saveMessage(String roomId, Message message) {
        return datastoreClient
                .post()
                .uri("/msg/{roomId}", roomId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new MessageDto(message.messageStr()))
                .retrieve()
                .bodyToMono(Long.class);
    }

    private Flux<Message> getMessagesByRoomId(String roomId) {
        return datastoreClient
                .get()
                .uri("/msg/{roomId}", roomId)
                .accept(MediaType.APPLICATION_NDJSON)
                .retrieve()
                .bodyToFlux(MessageDto.class)
                .map(dto -> new Message(dto.messageStr()));
    }
}
