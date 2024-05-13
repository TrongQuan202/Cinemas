package org.example.project_cinemas_java.service.iservice;

import org.example.project_cinemas_java.payload.request.seat_request.SeatStatusRequest;
import org.example.project_cinemas_java.payload.request.ticket_request.BookTicketRequest;

import java.util.List;

public interface ITicketService {
    String createTicketBySchedule(BookTicketRequest bookTicketRequest)throws Exception;

    List<SeatStatusRequest> updateSeatOfTicket () throws Exception;
}
