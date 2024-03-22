package org.example.project_cinemas_java.service.implement;

import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.model.Schedule;
import org.example.project_cinemas_java.model.Seat;
import org.example.project_cinemas_java.model.Ticket;
import org.example.project_cinemas_java.payload.request.ticket_request.BookTicketRequest;
import org.example.project_cinemas_java.repository.BillTicketRepo;
import org.example.project_cinemas_java.repository.ScheduleRepo;
import org.example.project_cinemas_java.repository.SeatRepo;
import org.example.project_cinemas_java.repository.TicketRepo;
import org.example.project_cinemas_java.service.iservice.ITicketService;
import org.example.project_cinemas_java.utils.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class TicketService implements ITicketService {
    @Autowired
    private ScheduleRepo scheduleRepo;
    @Autowired
    private BillTicketRepo billTicketRepo;
    @Autowired
    private TicketRepo ticketRepo;
    @Autowired
    private SeatRepo seatRepo;


    private String generateCode() {
        Random random = new Random();
        int randomNumber = random.nextInt(900000) + 100000;
        return String.valueOf(randomNumber);
    }
    @Override
    public String createTicketBySchedule(BookTicketRequest bookTicketRequest) throws Exception {
        int scheduleId = scheduleRepo.findScheduleIdsByMovieIdAndRoomIdAndDateTime(bookTicketRequest.getMovieId(),
                bookTicketRequest.getRoomId(),bookTicketRequest.getDayMonthYear(),bookTicketRequest.getStartTime());
        Schedule schedule = scheduleRepo.findById(scheduleId).orElse(null);
        if(schedule == null){
            throw new DataNotFoundException(MessageKeys.SCHEDULE_DOES_NOT_EXIST);
        }
        Seat seat = seatRepo.findById(bookTicketRequest.getSeatId()).orElse(null);
        if(seat == null){
            throw new DataNotFoundException(MessageKeys.SEAT_DOES_NOT_EXITS);
        }
        if(ticketRepo.existsBySeatAndScheduleNot(seat,schedule)){

        }
        if(ticketRepo.existsBySeatIdAndScheduleId(bookTicketRequest.getSeatId(),scheduleId)){

          }
//        Ticket ticket = ticketRepo.getTicketByScheduleIdAndSeatId(scheduleId,seatId);
        return null;
    }
}
