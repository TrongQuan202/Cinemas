//package org.example.project_cinemas_java.controller;
//
//import org.example.project_cinemas_java.payload.dto.seatdtos.SeatStatusDTO;
//import org.example.project_cinemas_java.payload.request.seat_request.SeatStatusRequest;
//import org.example.project_cinemas_java.service.implement.SeatService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.ReactiveAdapterRegistry;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.SendTo;
//import org.springframework.stereotype.Controller;
//
//
//@Controller
//public class MessageController {
//    @Autowired
//    private SeatService seatService;
//
//    @MessageMapping("/booking")
//    @SendTo("/topic/seatStatus")
//    public SeatStatusDTO send(SeatStatusRequest seatStatusRequest) throws Exception {
//
//        SeatStatusDTO seatStatusDTO = seatService.updateSeatStatus(seatStatusRequest);
//        return seatStatusDTO ;
//
//    }
//}
//
