package org.example.project_cinemas_java.service.implement;

import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.model.Room;
import org.example.project_cinemas_java.model.Seat;
import org.example.project_cinemas_java.model.SeatStatus;
import org.example.project_cinemas_java.model.SeatType;
import org.example.project_cinemas_java.payload.request.admin_request.seat_request.CreateSeatRequest;
import org.example.project_cinemas_java.payload.request.admin_request.seat_request.UpdateSeatRequest;
import org.example.project_cinemas_java.repository.RoomRepo;
import org.example.project_cinemas_java.repository.SeatRepo;
import org.example.project_cinemas_java.repository.SeatStatusRepo;
import org.example.project_cinemas_java.repository.SeatTypeRepo;
import org.example.project_cinemas_java.service.iservice.ISeatService;
import org.example.project_cinemas_java.utils.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
