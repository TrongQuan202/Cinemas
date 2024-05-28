package org.example.project_cinemas_java.repository;

import org.example.project_cinemas_java.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface TicketRepo extends JpaRepository<Ticket, Integer> {

    Ticket findTicketByScheduleAndSeat(Schedule schedule, Seat seat);
    List<Ticket> findAllByUserAndSeatStatus(User user, int seatStatus);



    List<Ticket> findAllByUserAndSeatStatusAndSchedule(User user, int seatStatus,Schedule schedule);

    boolean existsBySeatIdAndScheduleId(int seatId, int scheduleId);
    boolean existsBySeatAndScheduleNot(Seat seat, Schedule schedule);

//    Set<Ticket> findAllByBillTicketSet(Set<BillTicket> billTickets);
    List<Ticket> findAllByScheduleAndCodeNotNullAndPriceTicketGreaterThan(Schedule schedule, int value);

    Ticket findByUserAndSeat(User user, Seat seat);

    List<Ticket> findAllByUserAndSchedule(User user, Schedule schedule);

    List<Ticket> findAllByUserAndSeatTypeAndScheduleAndSeatStatus(User user, int seatType, Schedule schedule, int seatStatus);

    List<Ticket> findAllBySeatStatusAndTicketBookingTimeLessThan(int seatStatus, LocalDateTime localDateTime);

    List<Ticket> findAllBySchedule(Schedule schedule);


}
