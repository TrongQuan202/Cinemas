package org.example.project_cinemas_java.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;


@Controller
public class MessageController {
    @MessageMapping("/chat")
    @SendTo("/topic/message")
    public String send(String username) {
        return "Hello, " + username;
    }
}

