package org.example.project_cinemas_java.payload.converter;

import org.example.project_cinemas_java.model.ActorMovie;
import org.example.project_cinemas_java.model.Movie;
import org.example.project_cinemas_java.model.MovieRate;
import org.example.project_cinemas_java.model.MovieType;
import org.example.project_cinemas_java.payload.dto.moviedtos.MovieDTO;
import org.example.project_cinemas_java.payload.dto.moviedtos.MovieDetailDTO;
import org.example.project_cinemas_java.payload.dto.moviedtos.RateDTO;
import org.example.project_cinemas_java.repository.ActorMovieRepo;
import org.example.project_cinemas_java.repository.MovieRateRepo;
import org.example.project_cinemas_java.repository.MovieRepo;
import org.example.project_cinemas_java.repository.MovieTypeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Component
public class MovieConverter {
    @Autowired
    private MovieRepo movieRepo;
    @Autowired
    private MovieTypeRepo movieTypeRepo;
    @Autowired
    private ActorMovieRepo actorMovieRepo;
    @Autowired
    private MovieRateRepo movieRateRepo;

    public MovieDTO movieToMovieDTO (Movie movie){
        Set<String> listMovieTypeName = new HashSet<>();
        Set<MovieType> movieTypes = movieTypeRepo.findAllByMovie(movie);
        for (MovieType movieType:movieTypes){
            listMovieTypeName.add(movieType.getType().getMovieTypeName() == null ? null: movieType.getType().getMovieTypeName());
        }
        MovieDTO movieDTO = MovieDTO.builder()
                .movieName(movie.getName())
                .movieImage(movie.getImage())
                .movieDuration(movie.getMovieDuration())
                .movieTrailer(movie.getTrailer())
                .slug(movie.getSlug())
                .movieTypeName(listMovieTypeName)
                .isUpcoming(movie.isUpcoming())
                .build();
        return movieDTO;
    }

    private RateDTO calculateAverageRating(Movie movie){
        int totalStar = 0;
        int count = 0;
        for (MovieRate movieRate:movieRateRepo.findAllByMovie(movie)){
            totalStar += movieRate.getRate().getStarNumber();
            count++;
        }
        float rate = (float) totalStar/count;
        RateDTO rateDTO = RateDTO.builder()
                .totalVote(count)
                .vote(rate)
                .build();
        return rateDTO;
    }

    public MovieDetailDTO movieToMovieDetailDTO(Movie movie){
        List<String> actors = new ArrayList<>();
        for (ActorMovie actorMovie:actorMovieRepo.findAllByMovie(movie)){
            actors.add(actorMovie.getActor().getName());
        }

        Set<String> movieTypes = new HashSet<>();
        for (MovieType movieType:movieTypeRepo.findAllByMovie(movie)){
            movieTypes.add(movieType.getType().getMovieTypeName());
        }


        MovieDetailDTO movieDetailDTO = MovieDetailDTO.builder()
                .name(movie.getName())
                .image(movie.getImage())
                .description(movie.getDescription())
                .trailer(movie.getTrailer())
                .duration(movie.getMovieDuration())
                .startDate(movie.getPremiereDate().toLocalDate())
                .vote(calculateAverageRating(movie).getVote())
                .totalVote(calculateAverageRating(movie).getTotalVote())
                .language(movie.getLanguage())
                .movieType(movieTypes)
                .directors(movie.getDirector())
                .actors(actors)
                .heroImage(movie.getHerolmage())
                .build();
        return movieDetailDTO;
    }
}
