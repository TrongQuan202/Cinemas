package org.example.project_cinemas_java.service.iservice;

import org.example.project_cinemas_java.model.Movie;
import org.example.project_cinemas_java.payload.dto.moviedtos.*;
import org.example.project_cinemas_java.payload.request.admin_request.movie_request.CreateMovieRequest;

import org.example.project_cinemas_java.payload.request.movie_request.MovieTypeRequest;

import java.util.List;

public interface IMovieService {
    Movie createMovie(CreateMovieRequest createMovieRequest) throws Exception;

    CreateMovieRequest updateMovie(CreateMovieRequest createMovieRequest) throws Exception;

    List<MovieDTO>  getAllMovieByCinema(String nameOfCinema) throws Exception;

    MovieDetailDTO getMovieDetail(String slug)throws  Exception;

    MovieByScheduleDTO getMovieBySchedule(int scheduleId) throws Exception;

    List<MovieByAdminDTO> getMovieByAdmin();

    CreateMovieRequest getMovie(String slug)throws Exception;

    List<MovieTypeRequest> getAllMovieType();

    List<MovieScheduleAdminDTO> getAllMovieScheduleDTO();

    List<MovieSuggestDTO> getAllMovieSuggestDTO()throws Exception;
}
