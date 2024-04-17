package org.example.project_cinemas_java.payload.converter;

import org.example.project_cinemas_java.model.Movie;
import org.example.project_cinemas_java.model.MovieType;
import org.example.project_cinemas_java.payload.dto.moviedtos.MovieDTO;
import org.example.project_cinemas_java.repository.MovieRepo;
import org.example.project_cinemas_java.repository.MovieTypeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
@Component
public class MovieConverter {
    @Autowired
    private MovieRepo movieRepo;
    @Autowired
    private MovieTypeRepo movieTypeRepo;

    public MovieDTO movieToMovieDTO (Movie movie){
        Set<String> listMovieTypeName = new HashSet<>();
        Set<MovieType> movieTypes = movieTypeRepo.findAllByMovie(movie);
        for (MovieType movieType:movieTypes){
            listMovieTypeName.add(movieType.getType().getMovieTypeName());
        }
        MovieDTO movieDTO = MovieDTO.builder()
                .movieName(movie.getName())
                .movieImage(movie.getImage())
                .movieDuration(movie.getMovieDuration())
                .movieTrailer(movie.getTrailer())
                .slug(movie.getSlug())
                .movieTypeName(listMovieTypeName)
                .build();
        return movieDTO;
    }
}
