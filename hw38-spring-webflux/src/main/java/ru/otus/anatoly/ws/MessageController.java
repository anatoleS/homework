package ru.otus.anatoly.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.util.HtmlUtils;
import reactor.core.publisher.Flux;
import ru.otus.anatoly.domain.Message;
import ru.otus.anatoly.repository.MessageRepository;

@Controller
@Profile("client")
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    private static final String TOPIC_TEMPLATE = "/topic/response.";
    private static final String ROOM_1408 = "1408";

    private final SimpMessagingTemplate template;
    private final MessageRepository messageRepository;

    public MessageController(SimpMessagingTemplate template, MessageRepository messageRepository) {
        this.template = template;
        this.messageRepository = messageRepository;
    }

    @MessageMapping("/message.{roomId}")
    public void getMessage(@DestinationVariable("roomId") String roomId, Message message) {
        logger.info("get message:{}, roomId:{}", message, roomId);
        if (ROOM_1408.equals(roomId)) {
            logger.warn("Attempt to send message to read-only room 1408, ignoring");
            return;
        }
        messageRepository.save(message).subscribe(saved -> {
            logger.info("message saved id:{}", saved.id());
            var escaped = new Message(saved.id(), saved.roomId(), HtmlUtils.htmlEscape(saved.msgText()));
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

        Flux<Message> history;
        if (ROOM_1408.equals(roomId)) {
            history = messageRepository.findTop5ByOrderByIdDesc();
        } else {
            history = messageRepository.findByRoomId(roomId);
        }

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
}
