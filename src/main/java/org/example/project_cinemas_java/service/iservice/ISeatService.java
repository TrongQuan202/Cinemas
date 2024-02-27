package org.example.project_cinemas_java.service.iservice;

import org.example.project_cinemas_java.model.Seat;
import org.example.project_cinemas_java.payload.request.admin_request.seat_request.CreateSeatRequest;
import org.example.project_cinemas_java.payload.request.admin_request.seat_request.UpdateSeatRequest;

public interface ISeatService {
    Seat createSeat(CreateSeatRequest createSeatRequest) throws Exception;

    Seat updateSeat(UpdateSeatRequest updateSeatRequest) throws Exception;
}
