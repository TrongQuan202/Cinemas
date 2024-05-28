package org.example.project_cinemas_java.service.implement;

import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.model.*;
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
    private BillRepo billRepo;
    @Autowired
    private BillFoodRepo billFoodRepo;
    @Autowired
    private PromotionRepo promotionRepo;
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

    public void updateSeatHoldExpiration (int userId, String scheduleId) throws Exception{
        User user = userRepo.findById(userId).orElse(null);
        if(user == null){
            throw new DataNotFoundException("Thông tin  người dùng lỗi! Vui lòng truy cập lại");
        }
        Schedule schedule = scheduleRepo.findById(Integer.valueOf(scheduleId)).orElse(null);
        if(schedule == null){
            throw new DataNotFoundException("Lích chiếu không tồn tại! Vui lòng thử lại");
        }
        List<Ticket> tickets = ticketRepo.findAllByUserAndSeatStatusAndSchedule(user,3, schedule);

        if(!tickets.isEmpty()) {
            for (Ticket ticket:tickets){
                ticket.setSeatStatus(3);
                ticket.setTicketBookingTime(LocalDateTime.now());
                ticketRepo.save(ticket);
            }
        }else {
            throw new DataNotFoundException("Hết thời gian giữ ghế ! Vui lòng chọn lại ghế để tiếp tục");
        }

    }

    // Định nghĩa một scheduled task chạy mỗi phút
    @Scheduled(fixedRate = 5000)  // 60000 milliseconds = 1 minute
    @Transactional
    @Override
    public List<SeatStatusRequest> updateSeatOfTicket() throws Exception {
        // Lấy thời gian hiện tại trừ đi 5 phút
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(10); // thời gian giữ ghế

        List<Ticket> tickets = ticketRepo.findAllBySeatStatusAndTicketBookingTimeLessThan(3, fiveMinutesAgo);
        List<SeatStatusRequest> seatStatusRequests = new ArrayList<>();
        if(!tickets.isEmpty()){
            for (Ticket ticket:tickets){
                BillTicket billTicket = billTicketRepo.findByTicket(ticket);
                Bill bill = billTicket.getBill();
                if(bill != null){
                    bill.setTotalMoney(bill.getTotalMoney() - ticket.getPriceTicket());
                    bill.setPromotion(null);
                    billRepo.save(bill);
                    List<BillFood> billFoods = billFoodRepo.findAllByBill(bill);
                    if(!billFoods.isEmpty()){
                        for (BillFood billFood:billFoods){
                            billFood.setBill(null);
                            billFood.setFood(null);
                            billFood.setQuantity(0);
                            billFoodRepo.delete(billFood);
                        }
                    }
                    Promotion promotion = bill.getPromotion();
                    if(promotion != null){
                        promotion.setQuantity(promotion.getQuantity() + 1);
                        promotionRepo.save(promotion);
                    }
                }
                billTicket.setBill(null);
                billTicket.setTicket(null);
                billTicketRepo.delete(billTicket);

                ticket.setCode(null);
                ticket.setActive(false);
                ticket.setPriceTicket(0);
                ticket.setUser(null);
                ticket.setTicketBookingTime(null);
                ticket.setSeatStatus(1);
                ticketRepo.save(ticket);

                SeatStatusRequest seatStatusRequest = new SeatStatusRequest();
                seatStatusRequest.setSeatId(ticket.getSeat().getId());
                seatStatusRequest.setStatus(ticket.getSeatStatus());
                seatStatusRequest.setSchedule(ticket.getSchedule().getId());
                seatStatusRequest.setUserId(ticket.getUser() != null ? ticket.getUser().getId() : null);
                seatStatusRequest.setSeatType(ticket.getSeatType());
//                simpMessagingTemplate.convertAndSend("/topic/seatStatus/" + ticket.getSchedule().getId(), seatStatusRequest);
            }
        }


        return null;
    }
}
