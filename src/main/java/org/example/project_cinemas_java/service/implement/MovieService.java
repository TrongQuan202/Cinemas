package org.example.project_cinemas_java.service.implement;

import org.example.project_cinemas_java.exceptions.DataIntegrityViolationException;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.exceptions.InvalidMovieDataException;
import org.example.project_cinemas_java.model.*;
import org.example.project_cinemas_java.payload.converter.MovieConverter;
import org.example.project_cinemas_java.payload.dto.moviedtos.MovieByAdminDTO;
import org.example.project_cinemas_java.payload.dto.moviedtos.MovieByScheduleDTO;
import org.example.project_cinemas_java.payload.dto.moviedtos.MovieDTO;
import org.example.project_cinemas_java.payload.dto.moviedtos.MovieDetailDTO;
import org.example.project_cinemas_java.payload.request.admin_request.movie_request.CreateMovieRequest;
import org.example.project_cinemas_java.payload.request.admin_request.movie_request.UpdateMovieRequest;
import org.example.project_cinemas_java.payload.request.auth_request.RegisterRequest;
import org.example.project_cinemas_java.payload.request.movie_request.ActorRequest;
import org.example.project_cinemas_java.payload.request.movie_request.MovieTypeRequest;
import org.example.project_cinemas_java.repository.*;
import org.example.project_cinemas_java.service.iservice.IMovieService;
import org.example.project_cinemas_java.utils.MessageKeys;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MovieService implements IMovieService {
    @Autowired
    private MovieRepo movieRepo;
    @Autowired
    private MovieTypeRepo movieTypeRepo;
    @Autowired
    private TypeRepo typeRepo;
    @Autowired
    private RateRepo rateRepo;
    @Autowired
    private MovieTypeService movieTypeService;
    @Autowired
    private TypeService typeService;
    @Autowired
    private CinemaRepo cinemaRepo;
    @Autowired
    private MovieConverter movieConverter;
    @Autowired
    private ActorMovieRepo actorMovieRepo;
    @Autowired
    private ScheduleRepo scheduleRepo;
    @Autowired
    private ActorRepo actorRepo;
    @Autowired
    private ModelMapper modelMapper;

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
        //thêm cinema vào phim
        Cinema cinema = cinemaRepo.findBynameOfCinema(createMovieRequest.getCodeCinema());
        if(cinema == null){
            throw new DataNotFoundException(MessageKeys.CINEMA_DOES_NOT_EXIST);
        }
        boolean isUpcoming;
        if(createMovieRequest.getIsUpcoming() == "Phim sắp chiếu"){
            isUpcoming = false;
        }else {
            isUpcoming =true;
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
                .name(createMovieRequest.getName())
                .trailer(createMovieRequest.getTrailer())
                .slug(createMovieRequest.getSlug())
                .cinema(cinema)
                .isUpcoming(isUpcoming)
                .isActive(false)
                .build();
        movieRepo.save(movie);

        for (MovieTypeRequest type:createMovieRequest.getType()){
            Type type1 = typeRepo.findByMovieTypeName(type.getName());
            MovieType movieType = new MovieType();
            movieType.setMovie(movie);
            movieType.setType(type1);
            movieTypeRepo.save(movieType);
        }
        for (ActorRequest ac:createMovieRequest.getActor()){
            Actor actor = actorRepo.findByName(ac.getName());
            ActorMovie actorMovie = new ActorMovie();
            actorMovie.setMovie(movie);
            actorMovie.setMovie(movie);
            actorMovie.setActor(actor);
            actorMovieRepo.save(actorMovie);
        }

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
        movie.setName(updateMovieRequest.getName());
        movie.setTrailer(updateMovieRequest.getTrailer());
        movieRepo.save(movie);

        return movie;
    }

    @Override
    public List<MovieDTO> getAllMovieByCinema(String nameOfCinema) throws Exception{
        Cinema cinema = cinemaRepo.findBynameOfCinema(nameOfCinema);
        if(cinema == null){
            throw new DataNotFoundException(MessageKeys.CINEMA_DOES_NOT_EXIST);
        }
        List<MovieDTO> movieDTOS = new ArrayList<>();
        List<Movie> movies = movieRepo.findAllByCinema(cinema);
        for (Movie movie: movies){
            movieDTOS.add(movieConverter.movieToMovieDTO(movie));
        }
        return movieDTOS;
        }

    @Override
    public MovieDetailDTO getMovieDetail(String slug) throws Exception {
        Movie movie = movieRepo.findBySlug(slug);
        if(movie == null){
            throw new DataNotFoundException(MessageKeys.MOVIE_DOES_NOT_EXIST);
        }
        MovieDetailDTO movieDetailDTO = movieConverter.movieToMovieDetailDTO(movie);

        return movieDetailDTO;
    }

    @Override
    public MovieByScheduleDTO getMovieBySchedule(int scheduleId) throws Exception {
        Schedule schedule = scheduleRepo.findById(scheduleId).orElse(null);
        if(schedule == null){
            throw new DataNotFoundException(MessageKeys.SCHEDULE_DOES_NOT_EXIST);
        }

        MovieByScheduleDTO movieByScheduleDTO = new MovieByScheduleDTO();
        movieByScheduleDTO.setMovieName(schedule.getMovie().getName());
        Set<String> movieTypeName = new HashSet<>();
        for (MovieType movieType:movieTypeRepo.findAllByMovie(schedule.getMovie())){
            movieTypeName.add(movieType.getMovieTypeName());
        }
        movieByScheduleDTO.setMovieType(movieTypeName);
        movieByScheduleDTO.setImg(schedule.getMovie().getImage());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        movieByScheduleDTO.setDay(schedule.getStartAt().format(formatter));
        DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm");
        movieByScheduleDTO.setStartAt(schedule.getStartAt().format(format));
        movieByScheduleDTO.setCinemaName(schedule.getRoom().getCinema().getNameOfCinema());
        movieByScheduleDTO.setRomName(schedule.getRoom().getName());
        movieByScheduleDTO.setDuration(schedule.getMovie().getMovieDuration());

        return movieByScheduleDTO;
    }

    @Override
    public List<MovieByAdminDTO> getMovieByAdmin() {
        List<MovieByAdminDTO> movieByAdminDTOS = new ArrayList<>();
        for (Movie movie:movieRepo.findAll()){
            movieByAdminDTOS.add(movieToMovieByAdminDTO(movie));
        }
        return movieByAdminDTOS;
    }

    public MovieByAdminDTO movieToMovieByAdminDTO(Movie movie){
        return this.modelMapper.map(movie, MovieByAdminDTO.class);
    }
}
