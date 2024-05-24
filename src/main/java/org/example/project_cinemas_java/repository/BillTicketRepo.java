package org.example.project_cinemas_java.repository;

import org.example.project_cinemas_java.model.Bill;
import org.example.project_cinemas_java.model.BillTicket;
import org.example.project_cinemas_java.model.Schedule;
import org.example.project_cinemas_java.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface BillTicketRepo extends JpaRepository<BillTicket, Integer> {
    BillTicket findBillTicketByTicketAndBill(Ticket ticket, Bill bill);

    boolean existsByBillAndTicket(Bill bill, Ticket ticket);

    List<BillTicket> findAllByBill(Bill bill);


    List<BillTicket> findAllByTicketAndBill(Ticket ticket, Bill bill);

    BillTicket findByTicket(Ticket ticket);

    void deleteAllByBill(Bill bill);

    BillTicket findByBill(Bill bill);
}
