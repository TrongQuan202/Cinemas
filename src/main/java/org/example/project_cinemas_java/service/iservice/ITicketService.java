package org.example.project_cinemas_java.service.iservice;

import org.example.project_cinemas_java.payload.request.ticket_request.BookTicketRequest;

public interface ITicketService {
    String createTicketBySchedule(BookTicketRequest bookTicketRequest)throws Exception;
}
