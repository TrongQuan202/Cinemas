package org.example.project_cinemas_java.service.implement;

import org.example.project_cinemas_java.model.Bill;
import org.example.project_cinemas_java.model.BillTicket;
import org.example.project_cinemas_java.model.Ticket;
import org.example.project_cinemas_java.repository.BillRepo;
import org.example.project_cinemas_java.repository.BillTicketRepo;
import org.example.project_cinemas_java.repository.TicketRepo;
import org.example.project_cinemas_java.service.iservice.IBillTicketService;
import org.example.project_cinemas_java.utils.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class BillTicketService implements IBillTicketService {
    @Autowired
    private BillTicketRepo billTicketRepo;
    @Autowired
    private BillRepo billRepo;
    @Autowired
    private TicketRepo ticketRepo;


    @Override
    public void createBillTicket(Ticket ticket, Bill bill) throws Exception {
        if(billTicketRepo.findBillTicketByTicketAndBill(ticket,bill) != null){
            throw new DataIntegrityViolationException("BillTicket already exist");
        }
        BillTicket billTicket = new BillTicket();
        billTicket.setQuantity(0);
        billTicket.setTicket(ticket);
        billTicket.setBill(bill);
        billTicketRepo.save(billTicket);
    }
}
