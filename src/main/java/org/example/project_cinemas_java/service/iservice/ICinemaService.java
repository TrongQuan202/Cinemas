package org.example.project_cinemas_java.service.iservice;

import org.example.project_cinemas_java.model.Cinema;
import org.example.project_cinemas_java.payload.dto.cinemadtos.CinemaNameDTO;
import org.example.project_cinemas_java.payload.dto.cinemadtos.RevenueCinemaDTO;
import org.example.project_cinemas_java.payload.request.admin_request.cinema_request.CreateCinemaRequest;
import org.example.project_cinemas_java.payload.request.admin_request.cinema_request.UpdateCinemaRequest;

import java.util.List;

public interface ICinemaService {
    Cinema createCinema(CreateCinemaRequest createCinemaRequest) throws Exception;

    Cinema updateCinema(UpdateCinemaRequest updateCinemaRequest) throws Exception;

    String deleteCinema(int cinemaId) throws Exception;

    List<String> getCinemaByAddress(String address) throws Exception;

    List<Object[]> getRevenueListByCinema (int cinemaId, int year)throws Exception;

    List<String> getAllCinemaName();

    List<RevenueCinemaDTO> getRevenueCinema()throws Exception;
}
