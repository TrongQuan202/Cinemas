package org.example.project_cinemas_java.service.iservice;

import org.example.project_cinemas_java.model.Cinema;
import org.example.project_cinemas_java.payload.request.admin_request.cinema_request.CreateCinemaRequest;
import org.example.project_cinemas_java.payload.request.admin_request.cinema_request.UpdateCinemaRequest;

public interface ICinemaService {
    Cinema createCinema(CreateCinemaRequest createCinemaRequest) throws Exception;

    Cinema updateCinema(UpdateCinemaRequest updateCinemaRequest) throws Exception;

    String deleteCinema(int cinemaId) throws Exception;
}
