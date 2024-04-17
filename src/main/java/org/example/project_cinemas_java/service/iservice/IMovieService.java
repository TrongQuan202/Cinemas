package org.example.project_cinemas_java.service.iservice;

import org.example.project_cinemas_java.model.Movie;
import org.example.project_cinemas_java.payload.dto.moviedtos.MovieDTO;
import org.example.project_cinemas_java.payload.request.admin_request.movie_request.CreateMovieRequest;
import org.example.project_cinemas_java.payload.request.admin_request.movie_request.UpdateMovieRequest;

import java.util.List;

public interface IMovieService {
    Movie createMovie(CreateMovieRequest createMovieRequest) throws Exception;

    Movie updateMovie(UpdateMovieRequest updateMovieRequest) throws Exception;

    List<MovieDTO>  getAllMovieByCinema(String nameOfCinema) throws Exception;
}
