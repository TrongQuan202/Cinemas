package org.example.project_cinemas_java.service.implement;

import org.example.project_cinemas_java.exceptions.DataIntegrityViolationException;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.exceptions.InvalidMovieDataException;
import org.example.project_cinemas_java.model.*;
import org.example.project_cinemas_java.payload.converter.MovieConverter;
import org.example.project_cinemas_java.payload.dto.moviedtos.*;

import org.example.project_cinemas_java.payload.request.admin_request.movie_request.CreateMovieRequest;
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        // Phân tích chuỗi ngày giờ với formatter
        LocalDateTime localDateTime = LocalDateTime.parse(time, formatter);
        return localDateTime;
    }

    public boolean checkEndTimeAfterPremiereDate(String endTime, String premiereDate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        LocalDateTime endTimeDateTime = LocalDateTime.parse(endTime, formatter);
        LocalDateTime premiereDateTime = LocalDateTime.parse(premiereDate, formatter);
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
        if (movieRepo.existsByHerolmage(createMovieRequest.getHeroImage())){
            throw new DataIntegrityViolationException(MessageKeys.HERO_IMAGE_ALREADY_EXIST);
        }
        if(movieRepo.existsByTrailer(createMovieRequest.getTrailer())){
            throw new DataIntegrityViolationException(MessageKeys.TRAILER_ALREADY_EXIST);
        }
        //thêm cinema vào phim
        Cinema cinema = cinemaRepo.findBynameOfCinema(createMovieRequest.getCodeCinema());
        if(cinema == null){
            throw new DataNotFoundException(MessageKeys.CINEMA_DOES_NOT_EXIST);
        }
        Movie movie = Movie.builder()
                .movieDuration(createMovieRequest.getMovieDuration())
                .endTime(stringToLocalDateTime(createMovieRequest.getEndTime()))
                .premiereDate(stringToLocalDateTime(createMovieRequest.getPremiereDate()))
                .description(createMovieRequest.getDescription())
                .director(createMovieRequest.getDirector())
                .image("/img/" + createMovieRequest.getImage())
                .herolmage("/img/" +createMovieRequest.getHeroImage())
                .imageSuggest("/img/" +createMovieRequest.getImageSuggest())
                .language(createMovieRequest.getLanguage())
                .name(createMovieRequest.getName())
                .trailer(createMovieRequest.getTrailer())
                .slug(createMovieRequest.getSlug())
                .cinema(cinema)
                .isUpcoming(createMovieRequest.getIsUpcoming().equals("Phim sắp chiếu") ? true :false)
                .isActive(true)
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
    public CreateMovieRequest updateMovie(CreateMovieRequest createMovieRequest) throws Exception {
        Movie movie = movieRepo.findBySlug(createMovieRequest.getSlug());
        if(movie == null){
            throw new DataNotFoundException(MessageKeys.MOVIE_DOES_NOT_EXIST);
        }
        if(movieRepo.existsByHerolmageAndSlugNot(createMovieRequest.getHeroImage(), createMovieRequest.getSlug())){
            throw new DataIntegrityViolationException("Hero Image already exists");
        }
        if(movieRepo.existsByImageAndSlugNot(createMovieRequest.getImage(),createMovieRequest.getSlug())){
            throw new DataIntegrityViolationException("Image already exists");
        }
        if(movieRepo.existsByTrailerAndSlugNot(createMovieRequest.getTrailer(),createMovieRequest.getSlug())){
            throw new DataIntegrityViolationException("Trailer already exists");
        }
        if (!checkEndTimeAfterPremiereDate(createMovieRequest.getEndTime(),createMovieRequest.getPremiereDate())){
            throw new InvalidMovieDataException("The end time must be after the premiere time!");
        }

        System.out.println(createMovieRequest.getEndTime());
        boolean isUpcoming = Boolean.parseBoolean(null);
        if(createMovieRequest.getIsUpcoming().equals("Phim sắp chiếu")){
            isUpcoming = true;
        }else {
            isUpcoming =false;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

        movie.setMovieDuration(createMovieRequest.getMovieDuration());
        movie.setEndTime(LocalDateTime.parse(createMovieRequest.getEndTime(), formatter));

        movie.setPremiereDate(LocalDateTime.parse(createMovieRequest.getPremiereDate(), formatter));

        movie.setDescription(createMovieRequest.getDescription());
        movie.setDirector(createMovieRequest.getDirector());
        movie.setImage(createMovieRequest.getImage());
        movie.setHerolmage(createMovieRequest.getHeroImage());
        movie.setLanguage(createMovieRequest.getLanguage());
        movie.setName(createMovieRequest.getName());
        movie.setTrailer(createMovieRequest.getTrailer());
        movie.setUpcoming(isUpcoming);
        movie.setSlug(createMovieRequest.getSlug());
        movieRepo.save(movie);

        for (MovieType movieType:movieTypeRepo.findAllByMovie(movie)){
            movieType.setMovie(null);
            movieType.setType(null);
            movieTypeRepo.delete(movieType);
        }

        for (ActorMovie actorMovie:actorMovieRepo.findAllByMovie(movie)){
            actorMovie.setMovie(null);
            actorMovie.setActor(null);
            actorMovieRepo.delete(actorMovie);
        }


        return createMovieRequest;
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
            if(movie.isActive()){
                movieDTOS.add(movieConverter.movieToMovieDTO(movie));
            }

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
            movieTypeName.add(movieType.getType().getMovieTypeName());
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

    @Override
    public CreateMovieRequest getMovie(String slug) throws Exception {
        Movie movie = movieRepo.findBySlug(slug);
        if(movie == null){
            throw new DataNotFoundException(MessageKeys.MOVIE_DOES_NOT_EXIST);
        }
        CreateMovieRequest movieRequest = new CreateMovieRequest();

        List<ActorRequest> actorRequests = new ArrayList<>();
        for (ActorMovie actorMovie:actorMovieRepo.findAllByMovie(movie)){
            ActorRequest actorRequest = new ActorRequest();
            actorRequest.setName(actorMovie.getActor().getName());
            actorRequests.add(actorRequest);
        }

        List<MovieTypeRequest> movieTypeRequests = new ArrayList<>();
        for (MovieType movieType:movieTypeRepo.findAllByMovie(movie)){
            MovieTypeRequest movieTypeRequest = new MovieTypeRequest();
            movieTypeRequest.setName(movieType.getType().getMovieTypeName());
            movieTypeRequests.add(movieTypeRequest);
        }
        movieRequest.setMovieDuration(movie.getMovieDuration());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        movieRequest.setEndTime(movie.getEndTime().format(formatter));
        movieRequest.setPremiereDate(movie.getPremiereDate().format(formatter));
        movieRequest.setDescription(movie.getDescription());
        movieRequest.setDirector(movie.getDirector());
        movieRequest.setImage(movie.getImage());
        movieRequest.setHeroImage(movie.getHerolmage());
        movieRequest.setLanguage(movie.getLanguage());
        movieRequest.setName(movie.getName());
        movieRequest.setTrailer(movie.getTrailer());
        movieRequest.setSlug(movie.getSlug());
        movieRequest.setActor(actorRequests);
        movieRequest.setType(movieTypeRequests);
        movieRequest.setCodeCinema(movie.getCinema().getNameOfCinema());
        movieRequest.setIsUpcoming(movie.isUpcoming() ? "Phim đang chiếu" : "Phim sắp chiếu");

        return movieRequest;
    }

    @Override
    public List<MovieTypeRequest> getAllMovieType() {
        List<MovieTypeRequest> movieTypeRequests =new ArrayList<>();
        for (Type type :typeRepo.findAll()){
            MovieTypeRequest movieTypeRequest = new MovieTypeRequest();
            movieTypeRequest.setName(type.getMovieTypeName());
            movieTypeRequests.add(movieTypeRequest);
        }
        return movieTypeRequests;
    }

    @Override
    public List<MovieScheduleAdminDTO> getAllMovieScheduleDTO() {
        List<MovieScheduleAdminDTO> movieScheduleAdminDTOS = new ArrayList<>();
        for (Movie movie:movieRepo.findAll()){
            MovieScheduleAdminDTO movieScheduleAdminDTO = new MovieScheduleAdminDTO();
            movieScheduleAdminDTO.setName(movie.getName());
            movieScheduleAdminDTO.setId(movie.getId());
            movieScheduleAdminDTOS.add(movieScheduleAdminDTO);
        }
        return movieScheduleAdminDTOS;
    }

    @Override
    public List<MovieSuggestDTO> getAllMovieSuggestDTO() throws Exception {
        List<MovieSuggestDTO> movieSuggestDTOS = new ArrayList<>();
        int count = 0;
        for (Movie movie:movieRepo.findAll()){
            if(movie.getImageSuggest() != null){
                MovieSuggestDTO movieSuggestDTO = new MovieSuggestDTO();
                movieSuggestDTO.setMovieName(movie.getName());
                movieSuggestDTO.setSlug(movie.getSlug());
                movieSuggestDTO.setImage(movie.getImageSuggest());
                movieSuggestDTOS.add(movieSuggestDTO);
                count++;

                if (count == 3) {
                    break;
                }
            }
        }
        return movieSuggestDTOS;
    }


    public MovieByAdminDTO movieToMovieByAdminDTO(Movie movie){
        return this.modelMapper.map(movie, MovieByAdminDTO.class);
    }

    public void stopMovieShowing(String slug) throws Exception{
            Movie movie = movieRepo.findBySlug(slug);
            if(movie == null){
                throw new DataNotFoundException("Phim này không tồn tại! Vui lòng thử lại");
            }
            movie.setActive(false);
            movieRepo.save(movie);
    }
}
