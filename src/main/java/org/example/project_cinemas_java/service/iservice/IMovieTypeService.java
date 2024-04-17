package org.example.project_cinemas_java.service.iservice;

import org.example.project_cinemas_java.model.Movie;
import org.example.project_cinemas_java.model.MovieType;
import org.example.project_cinemas_java.model.Type;

public interface IMovieTypeService {
    MovieType createMovieType(Movie movie, Type type) throws Exception;
}
