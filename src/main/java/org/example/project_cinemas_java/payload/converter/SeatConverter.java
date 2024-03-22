package org.example.project_cinemas_java.payload.converter;

import org.example.project_cinemas_java.model.Seat;
import org.example.project_cinemas_java.payload.dto.seatdtos.SeatsByRoomDTO;
import org.springframework.stereotype.Component;

@Component
public class SeatConverter {
    public SeatsByRoomDTO seatToSeatByRoomDTO(Seat seat){
        SeatsByRoomDTO seatsByRoomDTO = new SeatsByRoomDTO();
        seatsByRoomDTO.setId(seat.getId());
        seatsByRoomDTO.setSeatLine(seat.getLine());
        seatsByRoomDTO.setSeatNumber(seat.getNumber());
        seatsByRoomDTO.setSeatStatus(seat.getSeatsStatus().getId());
        seatsByRoomDTO.setSeatType(seat.getSeatType().getId());
        return seatsByRoomDTO;
    }
}
