package org.example.project_cinemas_java.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class MessageController {

    @CrossOrigin()
    @MessageMapping("/ws")
    @SendTo("/topic/message")
    public String send(String username) {
        return "Hello, " + username;
    }
}
