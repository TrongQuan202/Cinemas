package org.example.project_cinemas_java.service.iservice;

import org.example.project_cinemas_java.model.Bill;
import org.example.project_cinemas_java.model.Ticket;

public interface IBillTicketService {
    void createBillTicket(Ticket ticket, Bill bill)throws Exception;
}
