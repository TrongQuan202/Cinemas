package org.example.project_cinemas_java.service.implement;

import org.example.project_cinemas_java.components.JwtTokenUtils;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.model.*;
import org.example.project_cinemas_java.payload.converter.SeatConverter;
import org.example.project_cinemas_java.payload.dto.seatdtos.*;
import org.example.project_cinemas_java.payload.request.admin_request.seat_request.CreateSeatRequest;
import org.example.project_cinemas_java.payload.request.admin_request.seat_request.UpdateSeatRequest;
import org.example.project_cinemas_java.payload.request.seat_request.SeatStatusRequest;
import org.example.project_cinemas_java.repository.*;
import org.example.project_cinemas_java.service.iservice.ISeatService;
import org.example.project_cinemas_java.utils.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class SeatService implements ISeatService {
    @Autowired
    private SeatRepo seatRepo;
    @Autowired
    private SeatTypeRepo seatTypeRepo;
    @Autowired
    private SeatStatusRepo seatStatusRepo;
    @Autowired
    private RoomRepo roomRepo;
    @Autowired
    private ScheduleRepo scheduleRepo;
    @Autowired
    private TicketRepo ticketRepo;
    @Autowired
    private BillRepo billRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private BillTicketRepo billTicketRepo;
    @Autowired
    private BillStatusRepo billStatusRepo;

    @Autowired
    private PromotionRepo promotionRepo;
    @Autowired
    private BillFoodRepo billFoodRepo;

    private final JwtTokenUtils jwtTokenUtils;

    private final TicketService ticketService;
    private final SeatConverter seatConverter;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;


    public SeatService(JwtTokenUtils jwtTokenUtils, SeatConverter seatConverter, TicketService ticketService) {
        this.jwtTokenUtils = jwtTokenUtils;
        this.seatConverter = seatConverter;

        this.ticketService = ticketService;
    }

    private String generateCode() {
        Random random = new Random();
        int randomNumber = random.nextInt(900000) + 100000;
        return String.valueOf(randomNumber);
    }

    @Override
    public Seat createSeat(CreateSeatRequest createSeatRequest) throws Exception {
        List<Seat> listSeatByNumberAndLine = seatRepo.findAllByNumberAndLine(createSeatRequest.getNumber(), createSeatRequest.getLine());
        SeatType seatType = seatTypeRepo.findById(createSeatRequest.getSeatTypeId()).orElse(null);
        SeatStatus seatStatus = seatStatusRepo.findById(createSeatRequest.getSeatStatusId()).orElse(null);
        Room room = roomRepo.findById(createSeatRequest.getRoomId()).orElse(null);
        if(!listSeatByNumberAndLine.isEmpty()){
            throw new DataIntegrityViolationException(MessageKeys.SEAT_ALREADY_EXIST);
        }
        if(seatType == null){
            throw new DataNotFoundException(MessageKeys.SEAT_TYPE_DOES_NOT_EXITS);
        }
        if(seatStatus == null){
            throw new DataNotFoundException(MessageKeys.SEAT_STATUS_DOES_NOT_EXITS);
        }
        if(room == null){
            throw new DataNotFoundException(MessageKeys.ROOM_DOES_NOT_EXIST);
        }

        Seat seat = Seat.builder()
                .number(createSeatRequest.getNumber())
                .seatType(seatType)
                .seatsStatus(seatStatus)
                .room(room)
                .line(createSeatRequest.getLine())
                .isActive(true)
                .build();
        seatRepo.save(seat);
        return seat;
    }

    @Override
    public Seat updateSeat(UpdateSeatRequest updateSeatRequest) throws Exception {
        Seat seat = seatRepo.findById(updateSeatRequest.getSeatId()).orElse(null);
        SeatStatus seatStatus = seatStatusRepo.findById(updateSeatRequest.getSeatStatusId()).orElse(null);
        SeatType seatType = seatTypeRepo.findById(updateSeatRequest.getSeatTypeId()).orElse(null);
        Room room = roomRepo.findById(updateSeatRequest.getRoomId()).orElse(null);
        if(seat == null){
            throw new DataNotFoundException(MessageKeys.SEAT_DOES_NOT_EXITS);
        }
        if(seatStatus == null){
            throw new DataNotFoundException(MessageKeys.SEAT_STATUS_DOES_NOT_EXITS);
        }
        if(seatType == null){
            throw new DataNotFoundException(MessageKeys.SEAT_TYPE_DOES_NOT_EXITS);
        }
        if(room == null){
            throw new DataNotFoundException(MessageKeys.ROOM_DOES_NOT_EXIST);
        }
        if(seatRepo.existsByLineAndNumberAndIdNot(updateSeatRequest.getLine(),updateSeatRequest.getNumber(), updateSeatRequest.getSeatId())){
            throw new DataIntegrityViolationException(MessageKeys.SEAT_ALREADY_EXIST);
        }
        seat.setSeatsStatus(seatStatus);
        seat.setSeatType(seatType);
        seat.setRoom(room);
        seat.setLine(updateSeatRequest.getLine());
        seat.setNumber(updateSeatRequest.getNumber());
        seatRepo.save(seat);

        return seat;
    }

    @Override
    public void updateStatusSeatsByScheduleAndRoom(String dayMonthYear, String startTime, int movieId, int roomId, int seatStatus, int seatId,String email) throws Exception {
        int scheduleId  = scheduleRepo.findScheduleIdsByStartAtAndMovie(startTime,dayMonthYear,movieId);
        Schedule schedule = scheduleRepo.findById(scheduleId).orElse(null);
        if(schedule == null){
            throw new DataNotFoundException(MessageKeys.SCHEDULE_DOES_NOT_EXIST);
        }

        Room room = roomRepo.findById(roomId).orElse(null);

        if(room == null){
            throw new DataNotFoundException(MessageKeys.ROOM_DOES_NOT_EXIST);
        }

//        List<SeatsByRoomDTO> seatsByRoomDTOS = new ArrayList<>();
        for (Seat seat:seatRepo.findAllByRoom(room)){
            if (seat.getId() == seatId && seatStatus == 3){
                seat.setSeatsStatus(seatStatusRepo.findById(seatStatus).orElse(null));
                seat.setSchedule(schedule);
                seatRepo.save(seat);

                //todo tạo vé sau khi user chọn ghế
                Ticket ticket = ticketRepo.findTicketByScheduleAndSeat(schedule,seat);
                ticket.setCode(generateCode());
                ticket.setActive(true);
                if(seat.getSeatType().getId() == 1){
                    ticket.setPriceTicket(schedule.getPrice());
                } else if (seat.getSeatType().getId() == 2) {
                    ticket.setPriceTicket(schedule.getPrice() + 5000);
                } else if (seat.getSeatType().getId() == 3) {
                    ticket.setPriceTicket((schedule.getPrice() + 5000)*2);
                }
                ticketRepo.save(ticket);

                //todo tạo billticket sau khi có vé
                //tìm user từ email
                User user = userRepo.findByEmail(email).orElse(null);
                if(user == null){
                    throw new DataNotFoundException(MessageKeys.USER_DOES_NOT_EXIST);
                }
                //toto tìm bill status có trạng thái là "chưa thanh toán"
                BillStatus billStatus = billStatusRepo.findById(3).orElse(null);
                if(billStatus == null){
                    throw new DataNotFoundException("Bill status doest not exit");
                }
                //tìm bill có trạng thái là "chưa thanh toán" từ user
                Bill bill = billRepo.findBillByUserAndBillstatus(user,billStatus);
                if(bill == null){
                    throw new DataNotFoundException("Bill does not exits");
                }
                //sau khi tìm thấy bill từ user thì lưu bill và ticket vào billTicket

                List<BillTicket> billTickets = billTicketRepo.findAllByBill(bill);

                if(billTicketRepo.findAllByTicketAndBill(ticket,bill).size() < 1 || billTickets.size() < 1){
                    BillTicket billTicket  = new BillTicket();
                    billTicket.setBill(bill);
                    billTicket.setTicket(ticket);
                    billTicket.setQuantity(0);
                    billTicketRepo.save(billTicket);
                } else if (billTicketRepo.findAllByTicketAndBill(ticket,bill).size() >=1 || billTickets.size() >= 1) {
                    for (BillTicket billTicket:billTickets){
                        if(billTicket.getTicket()== null){
                            billTicket.setTicket(ticket);
                            billTicketRepo.save(billTicket);
                        } else  {
                            continue;
                        }
                        break;
                    }
                }
            } else if (seat.getId() == seatId && seatStatus == 1) {
                seat.setSeatsStatus(seatStatusRepo.findById(seatStatus).orElse(null));
                seat.setSchedule(null);
                seatRepo.save(seat);

                Ticket ticket = ticketRepo.findTicketByScheduleAndSeat(schedule,seat);
                ticket.setCode(null);
                ticket.setPriceTicket(0);
                ticketRepo.save(ticket);

                User user = userRepo.findByEmail(email).orElse(null);
                BillStatus billStatus = billStatusRepo.findById(3).orElse(null);
                Bill bill = billRepo.findBillByUserAndBillstatus(user,billStatus);
                BillTicket billTicket = billTicketRepo.findBillTicketByTicketAndBill(ticket,bill);
                if(billTicket == null){
                    throw new DataNotFoundException("Bill does not exit");
                }

                billTicket.setTicket(null);
                billTicketRepo.save(billTicket);
//                Ticket ticket = ticketRepo.findTicketByScheduleAndSeat(schedule,seat);
//
//                BillTicket billTicket = billTicketRepo.findBillTicketByTicketAndBill(ticket,bill);
//                billTicket.setTicket(null);

            }
//            seatsByRoomDTOS.add(seatConverter.seatToSeatByRoomDTO(seat));
        }


    }

    @Override
    public List<SeatsByRoomDTO> getAllSeatByRoom(int roomId) throws Exception{
        Room room = roomRepo.findById(roomId).orElse(null);
        if(room == null){
            throw new DataNotFoundException(MessageKeys.ROOM_DOES_NOT_EXIST);
        }
        List<SeatsByRoomDTO> seatsByRoomDTOS = new ArrayList<>();
        for (Seat seat:seatRepo.findAllByRoom(room)){
            seatsByRoomDTOS.add(seatConverter.seatToSeatByRoomDTO(seat));
        }
        return seatsByRoomDTOS;
    }

    @Override
    public List<SeatsByRoomDTO> resetSeats(String dayMonthYear, String startTime, int movieId, int roomId) throws Exception {
        int scheduleId  = scheduleRepo.findScheduleIdsByStartAtAndMovie(startTime,dayMonthYear,movieId);
        Schedule schedule = scheduleRepo.findById(scheduleId).orElse(null);
        if(schedule == null){
            throw new DataNotFoundException(MessageKeys.SCHEDULE_DOES_NOT_EXIST);
        }

        Room room = roomRepo.findById(roomId).orElse(null);

        if(room == null){
            throw new DataNotFoundException(MessageKeys.ROOM_DOES_NOT_EXIST);
        }
        for (Seat seat:seatRepo.findAllByRoomAndSchedule(room,schedule)){
            if(seat.getSeatsStatus().getId() != 4){
                seat.setSeatsStatus(seatStatusRepo.findById(1).orElse(null));
                seat.setSchedule(null);
                seatRepo.save(seat);
            }
        }
        List<SeatsByRoomDTO> seatsByRoomDTOS = new ArrayList<>();
        for (Seat seat:seatRepo.findAllByRoom(room)){
            seatsByRoomDTOS.add(seatConverter.seatToSeatByRoomDTO(seat));
        }
        return seatsByRoomDTOS;
    }

    @Override
        public List<SeatsByRoomDTO> resetSeatByUser(String dayMonthYear, String startTime, int movieId, int roomId, String tokenOfUser) throws Exception {
        String email = jwtTokenUtils.extractEmail(tokenOfUser);
        User user = userRepo.findByEmail(email).orElse(null);
        if(email == null){
            throw new DataNotFoundException(MessageKeys.USER_DOES_NOT_EXIST);
        }
        //todo tìm bill của user mà chưa thanh toán ( bill status = 3 )
        Bill bill = billRepo.findBillByUserAndBillstatus(user,billStatusRepo.findById(3).orElse(null));

        //todo sau khi tìm được bill thì lấy ra list ticket tương ứng với bill đó
        List<BillTicket> billTickets = billTicketRepo.findAllByBill(bill);

        //todo sau khi thu đc danh sách bill ticket thì lấy ticket từ danh sách này
        Set<Ticket> tickets = new HashSet<>();
        for (BillTicket billTicket:billTickets){
            tickets.add(billTicket.getTicket());
        }

        //todo set tất cả ghế ngồi của bill này về 1 ( seat status về ghế trống )
        int scheduleId  = scheduleRepo.findScheduleIdsByStartAtAndMovie(startTime,dayMonthYear,movieId);
        Schedule schedule = scheduleRepo.findById(scheduleId).orElse(null);
        if(schedule == null){
            throw new DataNotFoundException(MessageKeys.SCHEDULE_DOES_NOT_EXIST);
        }

        Room room = roomRepo.findById(roomId).orElse(null);

        if(room == null){
            throw new DataNotFoundException(MessageKeys.ROOM_DOES_NOT_EXIST);
        }

        //todo lấy danh sách seat từ danh sách ticket
        Set<Seat> seats = new HashSet<>();
        for (Ticket ticket:tickets){
            if(ticket.getSchedule().getId() == scheduleId){
                seats.add(ticket.getSeat());
            }
        }
        if(seats.size() < 1){
            throw new DataNotFoundException("Ticket does not exit");
        }

        for (Seat seat:seats){
            if(seat.getSeatsStatus().getId() != 4
                    && seat.getRoom().getId() == roomId && seat.getSchedule().getId() == scheduleId){
                seat.setSchedule(null);
                seat.setSeatsStatus(seatStatusRepo.findById(1).orElse(null));
                seatRepo.save(seat);
            }
        }
        return null;
    }

    @Override
    public List<SeatsByRoomDTO> getAllSeat(int scheduleId) throws Exception {
        Schedule schedule = scheduleRepo.findById(scheduleId).orElse(null);
        if(schedule == null ){
            throw new DataNotFoundException(MessageKeys.SCHEDULE_DOES_NOT_EXIST);
        }
        List<SeatsByRoomDTO> seatsByRoomDTOS = new ArrayList<>();
            for (Seat seat:seatRepo.findAllByRoom(schedule.getRoom())){
            SeatsByRoomDTO seatsByRoomDTO = new SeatsByRoomDTO();
            seatsByRoomDTO.setId(seat.getId());
            seatsByRoomDTO.setScheduleId(scheduleId);
            seatsByRoomDTO.setSeatLine(seat.getLine());
            seatsByRoomDTO.setSeatNumber(seat.getNumber());
            seatsByRoomDTO.setSeatStatus(ticketRepo.findTicketByScheduleAndSeat(schedule,seat).getSeatStatus());
            seatsByRoomDTO.setSeatType(seat.getSeatType().getId());

            seatsByRoomDTOS.add(seatsByRoomDTO);
        }


        return seatsByRoomDTOS;
    }

    public float priceTicket(List<Ticket> tickets){
        float price = 0;
        for (Ticket ticket:tickets){
            price += ticket.getPriceTicket();
        }
        return price;
    }
    @Override
    public SeatSelectedDTO updateSeatStatus(SeatStatusRequest seatStatusRequest) throws Exception {
        User existingUser = userRepo.findById(seatStatusRequest.getUserId()).orElse(null);
        if(existingUser == null) {
            throw new DataNotFoundException(MessageKeys.USER_DOES_NOT_EXIST);
        }
        Seat seat = seatRepo.findById(seatStatusRequest.getSeatId()).orElse(null);
        if(seat == null) {
            throw new DataNotFoundException(MessageKeys.SEAT_DOES_NOT_EXITS);
        }
        Schedule schedule = scheduleRepo.findById(seatStatusRequest.getSchedule()).orElse(null);

        Ticket ticket = ticketRepo.findTicketByScheduleAndSeat(schedule,seat);

        if(ticket == null) {
            throw new DataNotFoundException("Ghế hoặc lịch không tồn tại");
        }
        if(ticket.getSeatStatus() == 3 && ticket.getUser().getId() != seatStatusRequest.getUserId()){
            throw new org.example.project_cinemas_java.exceptions.DataIntegrityViolationException("Ghế này đang được giữ! Vui lòng chọn ghế khác");
        }
        if(ticket.getSeatStatus() == 4){
            throw new org.example.project_cinemas_java.exceptions.DataIntegrityViolationException("Ghế này đã được bán! Vui lòng chọn ghế khác");
        }
        SeatSelectedDTO seatSelectedDTO = new SeatSelectedDTO();
//        Set<String> seats = new HashSet<>();
        //set lại seatStatus thành 3 ( đang được giữ )
        if(seatStatusRequest.getStatus() == 3 && seatStatusRequest.getSeatType() == 1){
            ticket.setCode(generateCode());
            ticket.setActive(true);
            ticket.setPriceTicket(schedule.getPrice() + 5000);
            ticket.setSeatStatus(3);
            ticket.setUser(existingUser);
            ticket.setSeatType(1);
            ticket.setTicketBookingTime(LocalDateTime.now());
            ticketRepo.save(ticket);


            Bill bill = billRepo.findBillByUserAndBillstatusId(existingUser,3);
            if(bill == null) {
                throw new DataNotFoundException("Bill does not exits");
            }

            //sau khi có bill và ticket thì luu vào bảng billTicket
            List<BillTicket> billTickets = billTicketRepo.findAllByBill(bill);
            if (billTickets.isEmpty()) {

                BillTicket billTicket = new BillTicket();
                billTicket.setBill(bill);
                billTicket.setTicket(ticket);
                billTicket.setQuantity(0);
                billTicketRepo.save(billTicket);
            } else {
                boolean ticketSet = false;

                for (BillTicket billTicket : billTickets) {
                    if (billTicket.getTicket() == null) {

                        System.out.println("ok");
                        billTicket.setTicket(ticket);
                        billTicketRepo.save(billTicket);
                        ticketSet = true;
                        break;
                    }
                }
                if (!ticketSet)  {
                    System.out.println(333);
                    BillTicket newBillTicket = new BillTicket();
                    newBillTicket.setBill(bill);
                    newBillTicket.setTicket(ticket);
                    newBillTicket.setQuantity(0);
                    billTicketRepo.save(newBillTicket);
                }
            }



//            if(billTicketRepo.findAllByTicketAndBill(ticket,bill).isEmpty() || billTickets.isEmpty()){
//
//            } else if (!billTicketRepo.findAllByTicketAndBill(ticket,bill).isEmpty()|| !billTickets.isEmpty()) {
//                for (BillTicket billTicket:billTickets){
//                    if(billTicket.getTicket() == null){
//                        billTicket.setTicket(ticket);
//                        billTicketRepo.save(billTicket);
//                    } else  {
//                        continue;
//                    }
//                    break;
//                }
//            }

            for (BillTicket billTicket1:billTicketRepo.findAllByTicketAndBill(ticket,bill)){
                bill.setTotalMoney(bill.getTotalMoney() + billTicket1.getTicket().getPriceTicket());
                billRepo.save(bill);
            }

            //tính toán số lượng ghế đã chọn và tổng tiền cho ghe thường
            seatSelectedDTO.setSeatType(1);
            List<Ticket> tickets = ticketRepo.findAllByUserAndSeatTypeAndScheduleAndSeatStatus(existingUser,1,schedule,3);
            seatSelectedDTO.setSeatSelectedCount(tickets.size());
            seatSelectedDTO.setPrice(priceTicket(tickets));
            seatSelectedDTO.setTotalMoney(bill.getTotalMoney());

            List<Ticket> ticketsByUserAndSchedule = ticketRepo.findAllByUserAndSchedule(existingUser,schedule);
            Set<String> seats = new HashSet<>();
            for (Ticket ticket1:ticketsByUserAndSchedule){
                if(ticket1.getSeatStatus() != 4){
                    seats.add(ticket1.getSeat().getLine() + ticket1.getSeat().getNumber());
                }
            }

            seatSelectedDTO.setSeatSelected(seats);

        }


        if(seatStatusRequest.getStatus() == 3 && seatStatusRequest.getSeatType() == 2){
            ticket.setCode(generateCode());
            ticket.setActive(true);
            ticket.setPriceTicket(schedule.getPrice() + 10000);
            ticket.setSeatStatus(3);
            ticket.setUser(existingUser);
            ticket.setSeatType(2);
            ticket.setTicketBookingTime(LocalDateTime.now());
            ticketRepo.save(ticket);

            Bill bill = billRepo.findBillByUserAndBillstatusId(existingUser,3);
            if(bill == null) {
                throw new DataNotFoundException("Bill does not exits");
            }

            //sau khi có bill và ticket thì luu vào bảng billTicket
            List<BillTicket> billTickets = billTicketRepo.findAllByBill(bill);
            if (billTickets.isEmpty()) {

                BillTicket billTicket = new BillTicket();
                billTicket.setBill(bill);
                billTicket.setTicket(ticket);
                billTicket.setQuantity(0);
                billTicketRepo.save(billTicket);
            } else {
                boolean ticketSet = false;

                for (BillTicket billTicket : billTickets) {
                    if (billTicket.getTicket() == null) {
                        billTicket.setTicket(ticket);
                        billTicketRepo.save(billTicket);
                        ticketSet = true;
                        break;
                    }
                }
                if (!ticketSet)  {

                    BillTicket newBillTicket = new BillTicket();
                    newBillTicket.setBill(bill);
                    newBillTicket.setTicket(ticket);
                    newBillTicket.setQuantity(0);
                    billTicketRepo.save(newBillTicket);
                }
            }

            for (BillTicket billTicket1:billTicketRepo.findAllByTicketAndBill(ticket,bill)){
                bill.setTotalMoney(bill.getTotalMoney() + billTicket1.getTicket().getPriceTicket());
                billRepo.save(bill);
            }

            //tính toán số lượng ghế đã chọn và tổng tiền cho ghe thường
            seatSelectedDTO.setSeatType(2);
            List<Ticket> tickets = ticketRepo.findAllByUserAndSeatTypeAndScheduleAndSeatStatus(existingUser,2,schedule,3);
            seatSelectedDTO.setSeatSelectedCount(tickets.size());
            seatSelectedDTO.setPrice(priceTicket(tickets));
            seatSelectedDTO.setTotalMoney(bill.getTotalMoney());
            List<Ticket> ticketsByUserAndSchedule = ticketRepo.findAllByUserAndSchedule(existingUser,schedule);
            Set<String> seats = new HashSet<>();
            for (Ticket ticket1:ticketsByUserAndSchedule){
                if(ticket1.getSeatStatus() != 4){
                    seats.add(ticket1.getSeat().getLine() + ticket1.getSeat().getNumber());
                }
            }

            seatSelectedDTO.setSeatSelected(seats);
        }


        if(seatStatusRequest.getStatus() == 3 && seatStatusRequest.getSeatType() == 3){
            ticket.setCode(generateCode());
            ticket.setActive(true);
            ticket.setPriceTicket((schedule.getPrice() + 10000)*2);
            ticket.setSeatStatus(3);
            ticket.setUser(existingUser);
            ticket.setSeatType(3);
            ticket.setTicketBookingTime(LocalDateTime.now());
            ticketRepo.save(ticket);

            Bill bill = billRepo.findBillByUserAndBillstatusId(existingUser,3);
            if(bill == null) {
                throw new DataNotFoundException("Bill does not exits");
            }

            //sau khi có bill và ticket thì luu vào bảng billTicket
            List<BillTicket> billTickets = billTicketRepo.findAllByBill(bill);
            if (billTickets.isEmpty()) {

                BillTicket billTicket = new BillTicket();
                billTicket.setBill(bill);
                billTicket.setTicket(ticket);
                billTicket.setQuantity(0);
                billTicketRepo.save(billTicket);
            } else {
                boolean ticketSet = false;

                for (BillTicket billTicket : billTickets) {
                    if (billTicket.getTicket() == null) {


                        billTicket.setTicket(ticket);
                        billTicketRepo.save(billTicket);
                        ticketSet = true;
                        break;
                    }
                }
                if (!ticketSet)  {

                    BillTicket newBillTicket = new BillTicket();
                    newBillTicket.setBill(bill);
                    newBillTicket.setTicket(ticket);
                    newBillTicket.setQuantity(0);
                    billTicketRepo.save(newBillTicket);
                }
            }
            for (BillTicket billTicket1:billTicketRepo.findAllByTicketAndBill(ticket,bill)){
                bill.setTotalMoney(bill.getTotalMoney() + billTicket1.getTicket().getPriceTicket());
                billRepo.save(bill);
            }

            //tính toán số lượng ghế đã chọn và tổng tiền cho ghe thường
            seatSelectedDTO.setSeatType(3);
            List<Ticket> tickets = ticketRepo.findAllByUserAndSeatTypeAndScheduleAndSeatStatus(existingUser,3,schedule,3);
            seatSelectedDTO.setSeatSelectedCount(tickets.size());
            seatSelectedDTO.setPrice(priceTicket(tickets));
            List<Ticket> ticketsByUserAndSchedule = ticketRepo.findAllByUserAndSchedule(existingUser,schedule);
            Set<String> seats = new HashSet<>();
            for (Ticket ticket1:ticketsByUserAndSchedule){
                if(ticket1.getSeatStatus() != 4){
                    seats.add(ticket1.getSeat().getLine() + ticket1.getSeat().getNumber());
                }

            }

            seatSelectedDTO.setSeatSelected(seats);
        }

        //set lại seatStatus thành 1 ( ghế trống )
        if(seatStatusRequest.getStatus() == 1 && seatStatusRequest.getSeatType() == 1){
            Bill bill = billRepo.findBillByUserAndBillstatusId(existingUser,3);
            if(bill == null) {
                throw new DataNotFoundException("Bill does not exits");
            }
            bill.setTotalMoney(bill.getTotalMoney() - ticket.getPriceTicket());
            billRepo.save(bill);

            ticket.setCode(null);
            ticket.setActive(false);
            ticket.setPriceTicket(0);
            ticket.setSeatStatus(1);
            ticket.setUser(null);
            ticket.setTicketBookingTime(null);
            ticketRepo.save(ticket);



            BillTicket billTicket = billTicketRepo.findBillTicketByTicketAndBill(ticket,bill);
            if(billTicket == null){
                throw new DataNotFoundException("BillTicket does not exits");
            }

            billTicket.setTicket(null);
//            billTicket.setBill(null);
//            billTicketRepo.delete(billTicket);
            billTicketRepo.save(billTicket);



            List<Ticket> tickets = ticketRepo.findAllByUserAndSeatTypeAndScheduleAndSeatStatus(existingUser,1,schedule,3);
            seatSelectedDTO.setSeatType(1);
            seatSelectedDTO.setSeatSelectedCount(tickets.size());
            seatSelectedDTO.setPrice(priceTicket(tickets));
            seatSelectedDTO.setTotalMoney(bill.getTotalMoney());
            List<Ticket> ticketsByUserAndSchedule = ticketRepo.findAllByUserAndSchedule(existingUser,schedule);
            Set<String> seats = new HashSet<>();
            for (Ticket ticket1:ticketsByUserAndSchedule){
                if(ticket1.getSeatStatus() !=4){
                    seats.add(ticket1.getSeat().getLine() + ticket1.getSeat().getNumber());
                }

            }

            seatSelectedDTO.setSeatSelected(seats);
        }

        if(seatStatusRequest.getStatus() == 1 && seatStatusRequest.getSeatType() == 2){
            Bill bill = billRepo.findBillByUserAndBillstatusId(existingUser,3);
            if(bill == null) {
                throw new DataNotFoundException("Bill does not exits");
            }
            bill.setTotalMoney(bill.getTotalMoney() - ticket.getPriceTicket());
            billRepo.save(bill);

            ticket.setCode(null);
            ticket.setActive(false);
            ticket.setPriceTicket(0);
            ticket.setSeatStatus(1);
            ticket.setUser(null);
            ticket.setTicketBookingTime(null);
            ticketRepo.save(ticket);



            BillTicket billTicket = billTicketRepo.findBillTicketByTicketAndBill(ticket,bill);
            if(billTicket == null){
                throw new DataNotFoundException("BillTicket does not exits");
            }
            billTicket.setTicket(null);
//            billTicket.setBill(null);
            billTicketRepo.save(billTicket);


            List<Ticket> tickets = ticketRepo.findAllByUserAndSeatTypeAndScheduleAndSeatStatus(existingUser,2,schedule,3);
            seatSelectedDTO.setSeatType(2);
            seatSelectedDTO.setSeatSelectedCount(tickets.size());
            seatSelectedDTO.setPrice(priceTicket(tickets));
            seatSelectedDTO.setTotalMoney(bill.getTotalMoney());
            List<Ticket> ticketsByUserAndSchedule = ticketRepo.findAllByUserAndSchedule(existingUser,schedule);
            Set<String> seats = new HashSet<>();
            for (Ticket ticket1:ticketsByUserAndSchedule){
                seats.add(ticket1.getSeat().getLine() + ticket1.getSeat().getNumber());
            }

            seatSelectedDTO.setSeatSelected(seats);
        }

        if(seatStatusRequest.getStatus() == 1 && seatStatusRequest.getSeatType() == 3){
            Bill bill = billRepo.findBillByUserAndBillstatusId(existingUser,3);
            if(bill == null) {
                throw new DataNotFoundException("Bill does not exits");
            }
            bill.setTotalMoney(bill.getTotalMoney() - ticket.getPriceTicket());
            billRepo.save(bill);

            ticket.setCode(null);
            ticket.setActive(false);
            ticket.setPriceTicket(0);
            ticket.setSeatStatus(1);
            ticket.setUser(null);
            ticket.setTicketBookingTime(null);
            ticketRepo.save(ticket);

            BillTicket billTicket = billTicketRepo.findBillTicketByTicketAndBill(ticket,bill);
            if(billTicket == null){
                throw new DataNotFoundException("BillTicket does not exits");
            }

            billTicket.setTicket(null);
//            billTicket.setBill(null);
            billTicketRepo.save(billTicket);

            List<Ticket> tickets = ticketRepo.findAllByUserAndSeatTypeAndScheduleAndSeatStatus(existingUser,3,schedule,3);
            seatSelectedDTO.setSeatType(3);
            seatSelectedDTO.setSeatSelectedCount(tickets.size());
            seatSelectedDTO.setPrice(priceTicket(tickets));
            seatSelectedDTO.setTotalMoney(bill.getTotalMoney());
            List<Ticket> ticketsByUserAndSchedule = ticketRepo.findAllByUserAndSchedule(existingUser,schedule);
            Set<String> seats = new HashSet<>();
            for (Ticket ticket1:ticketsByUserAndSchedule){
                if(ticket1.getSeatStatus() != 4){
                    seats.add(ticket1.getSeat().getLine() + ticket1.getSeat().getNumber());
                }

            }

            seatSelectedDTO.setSeatSelected(seats);
        }

//        SeatStatusDTO seatStatusDTO = SeatStatusDTO.builder()
//                .userId(ticket.getUser() != null ? ticket.getUser().getId() : null)
//                .seatStatus(ticket.getSeatStatus())
//                .id(ticket.getSeat().getId())
//                .build();

//        simpMessagingTemplate.convertAndSend("/topic/seatStatus/" + seatStatusRequest.getSchedule(), seatStatusRequest );
        return seatSelectedDTO;
    }

    @Override
    public List<SeatStatusDTO> resetSeatStatusByUser(String email, int scheduleId) throws Exception {
        Optional<User> user = userRepo.findByEmail(email);
        if(user.isEmpty()){
            throw new DataNotFoundException(MessageKeys.USER_DOES_NOT_EXIST);
        }
        Schedule schedule = scheduleRepo.findById(scheduleId).orElse(null);
        if(schedule == null){
            throw new DataNotFoundException(MessageKeys.SCHEDULE_DOES_NOT_EXIST);
        }
        List<SeatStatusDTO> seatStatusDTOS = new ArrayList<>();
        List<Ticket> tickets = ticketRepo.findAllByUserAndSchedule(user.get(),schedule);
        for (Ticket ticket:tickets){
            if(ticket.getSeatStatus() == 3){
                ticket.setPriceTicket(0);
                ticket.setCode(null);
                ticket.setActive(false);
                ticket.setSeatStatus(1);
                ticket.setUser(null);
                ticket.setTicketBookingTime(null);
                ticketRepo.save(ticket);

                Bill bill = billRepo.findBillByUserAndBillstatusId(user.get(),3);
                if(bill == null) {
                    throw new DataNotFoundException("Bill does not exits");
                }
                if(bill.getPromotion() != null){
                    Promotion promotion = bill.getPromotion();
                    System.out.println(promotion.getQuantity());
                    promotion.setQuantity(promotion.getQuantity() + 1);
                    System.out.println("helllo");
                    promotionRepo.save(promotion);
                }

                List<BillFood> billFoods = billFoodRepo.findAllByBill(bill);
                if (!billFoods.isEmpty()){
                    for (BillFood billFood:billFoods){
                        bill.setTotalMoney(bill.getTotalMoney() - billFood.getFood().getPrice());
                        billRepo.save(bill);
                        billFood.setFood(null);
                        billFood.setBill(null);
                        billFood.setQuantity(0);
                        billFoodRepo.delete(billFood);
                    }
                }
                bill.setTotalMoney(0);
                billRepo.save(bill);

                List<BillTicket> billTickets = billTicketRepo.findAllByBill(bill);
                if(!billTickets.isEmpty()){
                    for (BillTicket billTicket:billTickets){
                        billTicket.setTicket(null);
                        billTicket.setBill(null);
                        billTicketRepo.deleteById(billTicket.getId());
                    }
                }


                SeatStatusDTO seatStatusDTO = new SeatStatusDTO();
                seatStatusDTO.setSeatStatus(ticket.getSeatStatus());
                seatStatusDTO.setId(ticket.getSeat().getId());
                seatStatusDTO.setUserId(null);
                seatStatusDTOS.add(seatStatusDTO);

                SeatStatusRequest seatStatusRequest = new SeatStatusRequest();
                seatStatusRequest.setSeatId(ticket.getSeat().getId());
                seatStatusRequest.setStatus(ticket.getSeatStatus());
                seatStatusRequest.setSchedule(ticket.getSchedule().getId());
                seatStatusRequest.setUserId(ticket.getUser() != null ? ticket.getUser().getId() : null);
                seatStatusRequest.setSeatType(ticket.getSeatType());
                simpMessagingTemplate.convertAndSend("/topic/seatStatus/" + ticket.getSchedule().getId(), seatStatusRequest);
            }

        }
        return seatStatusDTOS;
    }
}
