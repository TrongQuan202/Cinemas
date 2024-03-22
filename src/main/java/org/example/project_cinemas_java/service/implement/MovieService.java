package org.example.project_cinemas_java.service.implement;

import org.example.project_cinemas_java.exceptions.DataIntegrityViolationException;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.exceptions.InvalidMovieDataException;
import org.example.project_cinemas_java.model.Movie;
import org.example.project_cinemas_java.payload.dto.moviedtos.MovieTicketCountDTO;
import org.example.project_cinemas_java.payload.request.admin_request.movie_request.CreateMovieRequest;
import org.example.project_cinemas_java.payload.request.admin_request.movie_request.UpdateMovieRequest;
import org.example.project_cinemas_java.repository.MovieRepo;
import org.example.project_cinemas_java.repository.MovieTypeRepo;
import org.example.project_cinemas_java.repository.RateRepo;
import org.example.project_cinemas_java.service.iservice.IMovieService;
import org.example.project_cinemas_java.utils.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieService implements IMovieService {
    @Autowired
    private MovieRepo movieRepo;
    @Autowired
    private MovieTypeRepo movieTypeRepo;
    @Autowired
    private RateRepo rateRepo;

    public LocalDateTime stringToLocalDateTime (String time){
        DateTimeFormatter endTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate localDate =LocalDate.parse(time,endTimeFormatter);
        LocalDateTime localDateTimeEndTime = localDate.atStartOfDay();
        return localDateTimeEndTime;
    }

    public boolean checkEndTimeAfterPremiereDate(String endTime, String premiereDate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime endTimeDateTime = LocalDateTime.parse(endTime + " 00:00:00", formatter);
        LocalDateTime premiereDateTime = LocalDateTime.parse(premiereDate + " 00:00:00", formatter);
        if (endTimeDateTime.isBefore(premiereDateTime)) {
            return false;
        }
            return true;
    }
    @Override
    public Movie createMovie(CreateMovieRequest createMovieRequest) throws Exception {

        if(!movieTypeRepo.existsById(createMovieRequest.getMovieTypeId())){
            throw new DataNotFoundException(MessageKeys.MOVIE_TYPE_DOES_NOT_EXIST);
        }
        if(!rateRepo.existsById(createMovieRequest.getRateId())){
            throw new DataNotFoundException(MessageKeys.RATE_DOES_NOT_EXIST);
        }
        if(movieRepo.existsByImage(createMovieRequest.getImage())){
              throw  new DataIntegrityViolationException(MessageKeys.IMAGE_ALREADY_EXIST);
        }
        if (movieRepo.existsByHerolmage(createMovieRequest.getHerolmage())){
            throw new DataIntegrityViolationException(MessageKeys.HERO_IMAGE_ALREADY_EXIST);
        }
        if(movieRepo.existsByTrailer(createMovieRequest.getTrailer())){
            throw new DataIntegrityViolationException(MessageKeys.TRAILER_ALREADY_EXIST);
        }
        if (!checkEndTimeAfterPremiereDate(createMovieRequest.getEndTime(),createMovieRequest.getPremiereDate())){
            throw new InvalidMovieDataException("The end time must be after the premiere time!");
        }

        Movie movie = Movie.builder()
                .movieDuration(createMovieRequest.getMovieDuration())
                .endTime(stringToLocalDateTime(createMovieRequest.getEndTime()))
                .premiereDate(stringToLocalDateTime(createMovieRequest.getPremiereDate()))
                .description(createMovieRequest.getDescription())
                .director(createMovieRequest.getDirector())
                .image(createMovieRequest.getImage())
                .herolmage(createMovieRequest.getHerolmage())
                .language(createMovieRequest.getLanguage())
                .movietype(movieTypeRepo.findById(createMovieRequest.getMovieTypeId()).orElse(null))
                .name(createMovieRequest.getName())
                .rate(rateRepo.findById(createMovieRequest.getRateId()).orElse(null))
                .trailer(createMovieRequest.getTrailer())
                .isActive(false)
                .build();
        movieRepo.save(movie);

        return movie;
    }

    @Override
    public Movie updateMovie(UpdateMovieRequest updateMovieRequest) throws Exception {
        Movie movie = movieRepo.findById(updateMovieRequest.getMovieId()).orElse(null);
        if(movie == null){
            throw new DataNotFoundException(MessageKeys.MOVIE_DOES_NOT_EXIST);
        }
        if(movieRepo.existsByHerolmageAndIdNot(updateMovieRequest.getHerolmage(), updateMovieRequest.getMovieId())){
            throw new DataIntegrityViolationException("Hero Image already exists");
        }
        if(movieRepo.existsByImageAndIdNot(updateMovieRequest.getImage(),updateMovieRequest.getMovieId())){
            throw new DataIntegrityViolationException("Image already exists");
        }
        if(movieRepo.existsByTrailerAndIdNot(updateMovieRequest.getTrailer(),updateMovieRequest.getMovieId())){
            throw new DataIntegrityViolationException("Trailer already exists");
        }
        if (!checkEndTimeAfterPremiereDate(updateMovieRequest.getEndTime(),updateMovieRequest.getPremiereDate())){
            throw new InvalidMovieDataException("The end time must be after the premiere time!");
        }
        if(!movieTypeRepo.existsById(updateMovieRequest.getMovieTypeId())){
            throw new DataNotFoundException(MessageKeys.MOVIE_TYPE_DOES_NOT_EXIST);
        }
        if(!rateRepo.existsById(updateMovieRequest.getRateId())){
            throw new DataNotFoundException(MessageKeys.RATE_DOES_NOT_EXIST);
        }
        movie.setMovieDuration(updateMovieRequest.getMovieDuration());
        movie.setEndTime(stringToLocalDateTime(updateMovieRequest.getEndTime()));
        movie.setPremiereDate(stringToLocalDateTime(updateMovieRequest.getPremiereDate()));
        movie.setDescription(updateMovieRequest.getDescription());
        movie.setDirector(updateMovieRequest.getDirector());
        movie.setImage(updateMovieRequest.getImage());
        movie.setHerolmage(updateMovieRequest.getHerolmage());
        movie.setLanguage(updateMovieRequest.getLanguage());
        movie.setMovietype(movieTypeRepo.findById(updateMovieRequest.getMovieTypeId()).orElse(null));
        movie.setName(updateMovieRequest.getName());
        movie.setRate(rateRepo.findById(updateMovieRequest.getRateId()).orElse(null));
        movie.setTrailer(updateMovieRequest.getTrailer());
        movieRepo.save(movie);

        return movie;
    }

    @Override
    public List<MovieTicketCountDTO> getMoviesOrderByTicketCount(String nameOfCinema) {
        List<Object[]> movieTicketCounts = movieRepo.findMoviesOrderByTicketCount(nameOfCinema);
        return movieTicketCounts.stream()
                .map(objects -> {
                    Integer movieId = (Integer) objects[0];
                    String movieName = (String) objects[1];
                    String movieImage = (String) objects[2];
                    Integer movieDuration = (Integer) objects[3];
                    String movieTrailer = (String) objects[4];
                    String movieTypeName = (String) objects[5];
                    String movieDescription = (String) objects[6];
                    String movieDirector = (String) objects[7];
                    String movieLanguage = (String) objects[8];
                    // Chuyển đổi Timestamp thành LocalDateTime
                    Timestamp timestamp = (Timestamp) objects[9];
                    String moviePremiereDate = timestamp != null ? timestamp.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null;

                    return new MovieTicketCountDTO(movieId, movieName, movieImage, movieDuration, movieTrailer,
                            movieTypeName,movieDescription,movieDirector,movieLanguage, moviePremiereDate);
                })
                .collect(Collectors.toList());
    }

    }
