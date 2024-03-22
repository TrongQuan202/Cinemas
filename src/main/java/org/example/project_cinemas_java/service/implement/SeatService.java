package org.example.project_cinemas_java.service.implement;

import org.example.project_cinemas_java.components.JwtTokenUtils;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.model.*;
import org.example.project_cinemas_java.payload.converter.SeatConverter;
import org.example.project_cinemas_java.payload.dto.seatdtos.SeatsByRoomDTO;
import org.example.project_cinemas_java.payload.request.admin_request.seat_request.CreateSeatRequest;
import org.example.project_cinemas_java.payload.request.admin_request.seat_request.UpdateSeatRequest;
import org.example.project_cinemas_java.repository.*;
import org.example.project_cinemas_java.service.iservice.ISeatService;
import org.example.project_cinemas_java.utils.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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

    private final JwtTokenUtils jwtTokenUtils;

    private final TicketService ticketService;
    private final SeatConverter seatConverter;



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

                Set<BillTicket> billTickets = billTicketRepo.findAllByBill(bill);

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
        Set<BillTicket> billTickets = billTicketRepo.findAllByBill(bill);

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
}
