package org.example.project_cinemas_java.service.iservice;

import org.example.project_cinemas_java.model.Seat;
import org.example.project_cinemas_java.model.User;
import org.example.project_cinemas_java.payload.dto.seatdtos.SeatSelectedDTO;
import org.example.project_cinemas_java.payload.dto.seatdtos.SeatStatusDTO;
import org.example.project_cinemas_java.payload.dto.seatdtos.SeatTotalMoneyDTO;
import org.example.project_cinemas_java.payload.dto.seatdtos.SeatsByRoomDTO;
import org.example.project_cinemas_java.payload.request.admin_request.seat_request.CreateSeatRequest;
import org.example.project_cinemas_java.payload.request.admin_request.seat_request.UpdateSeatRequest;
import org.example.project_cinemas_java.payload.request.seat_request.SeatStatusRequest;

import java.util.List;

public interface ISeatService {
    Seat createSeat(CreateSeatRequest createSeatRequest) throws Exception;

    Seat updateSeat(UpdateSeatRequest updateSeatRequest) throws Exception;

    void updateStatusSeatsByScheduleAndRoom(String dayMonthYear, String startTime, int movieId, int roomId,int seatStatus, int seatId, String email) throws Exception;

    List<SeatsByRoomDTO> getAllSeatByRoom(int roomId) throws Exception;

    List<SeatsByRoomDTO> resetSeats(String dayMonthYear, String startTime, int movieId, int roomId) throws Exception;

    List<SeatsByRoomDTO> resetSeatByUser(String dayMonthYear, String startTime, int movieId, int roomId,String tokenOfUser) throws Exception;

    List<SeatsByRoomDTO> getAllSeat(int scheduleId) throws Exception;

    SeatSelectedDTO updateSeatStatus(SeatStatusRequest seatStatusRequest) throws Exception;

    List<SeatStatusDTO> resetSeatStatusByUser(String  email, int schedule) throws Exception;
}
