package org.example.project_cinemas_java.controller;

import org.example.project_cinemas_java.payload.dto.seatdtos.SeatStatusDTO;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;


@Controller
public class MessageController {


    @MessageMapping("/booking")
    @SendTo("/topic/seatStatus")
    public String send(SeatStatusDTO seatStatusDTO) {
        return "Hello," + "status:" + seatStatusDTO.getSeatStatus() + "Id:" +seatStatusDTO.getUser() ;

    }
}

