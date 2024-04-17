package org.example.project_cinemas_java.service.implement;

import org.example.project_cinemas_java.exceptions.DataIntegrityViolationException;
import org.example.project_cinemas_java.model.Movie;
import org.example.project_cinemas_java.model.MovieType;
import org.example.project_cinemas_java.model.Type;
import org.example.project_cinemas_java.repository.MovieTypeRepo;
import org.example.project_cinemas_java.service.iservice.IMovieTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MovieTypeService implements IMovieTypeService {

    @Autowired
    private MovieTypeRepo movieTypeRepo;

    @Override
    public MovieType createMovieType(Movie movie, Type type) throws Exception {
        if(movieTypeRepo.existsByMovieAndType(movie,type)){
            throw new DataIntegrityViolationException("MovieType early exits");
        }

        MovieType movieType = MovieType.builder()
                .movie(movie)
                .type(type)
                .isActive(true)
                .build();
        return movieTypeRepo.save(movieType);
    }
}
