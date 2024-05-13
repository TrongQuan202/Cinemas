package org.example.project_cinemas_java.service.implement;

import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.model.Schedule;
import org.example.project_cinemas_java.model.Seat;
import org.example.project_cinemas_java.model.Ticket;
import org.example.project_cinemas_java.model.User;
import org.example.project_cinemas_java.payload.request.seat_request.SeatStatusRequest;
import org.example.project_cinemas_java.payload.request.ticket_request.BookTicketRequest;
import org.example.project_cinemas_java.repository.*;
import org.example.project_cinemas_java.service.iservice.ITicketService;
import org.example.project_cinemas_java.utils.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

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

    public void updateSeatHoldExpiration (int userId) throws Exception{
        User user = userRepo.findById(userId).orElse(null);
        if(user == null){
            throw new DataNotFoundException("Thông tin  người dùng lỗi! Vui lòng truy cập lại");
        }
        List<Ticket> tickets = ticketRepo.findAllByUserAndSeatStatus(user,3);
        if(tickets != null) {
            for (Ticket ticket:tickets){
                ticket.setSeatStatus(3);
                ticket.setTicketBookingTime(LocalDateTime.now());
                ticketRepo.save(ticket);
            }
        }

    }

    // Định nghĩa một scheduled task chạy mỗi phút
    @Scheduled(fixedRate = 10000)  // 60000 milliseconds = 1 minute
    @Transactional
    @Override
    public List<SeatStatusRequest> updateSeatOfTicket() throws Exception {
        // Lấy thời gian hiện tại trừ đi 5 phút
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(10);

        List<Ticket> tickets = ticketRepo.findAllBySeatStatusAndTicketBookingTimeLessThan(3, fiveMinutesAgo);
        List<SeatStatusRequest> seatStatusRequests = new ArrayList<>();
        for (Ticket ticket:tickets){
            ticket.setCode(null);
            ticket.setPriceTicket(0);
            ticket.setActive(false);
            ticket.setUser(null);
            ticket.setTicketBookingTime(null);
            ticket.setSeatStatus(1);
            ticketRepo.save(ticket);
//            SeatStatusRequest seatStatusRequest = new SeatStatusRequest();
//            seatStatusRequest.setSeatId(ticket.getSeat().getId());
//            seatStatusRequest.setStatus(ticket.getSeatStatus());
//            seatStatusRequest.setSchedule(ticket.getSchedule().getId());
//            seatStatusRequest.setUserId(ticket.getUser().getId());
//            seatStatusRequest.setSeatType(ticket.getSeatType());
//            seatStatusRequests.add(seatStatusRequest);
        }

        return null;
    }
}
