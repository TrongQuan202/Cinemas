package org.example.project_cinemas_java.service.implement;

import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.model.Movie;
import org.example.project_cinemas_java.model.Schedule;
import org.example.project_cinemas_java.payload.dto.scheduledtos.*;
import org.example.project_cinemas_java.payload.request.admin_request.schedule_request.CreateScheduleRequest;
import org.example.project_cinemas_java.repository.MovieRepo;
import org.example.project_cinemas_java.repository.RoomRepo;
import org.example.project_cinemas_java.repository.ScheduleRepo;
import org.example.project_cinemas_java.service.iservice.IScheduleService;
import org.example.project_cinemas_java.utils.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ScheduleService implements IScheduleService {
    @Autowired
    private ScheduleRepo scheduleRepo;
    @Autowired
    private MovieRepo movieRepo;

    @Autowired
    private RoomRepo roomRepo;

    @Override
    public List<String> getAllDayMonthYearOfScheduleByMovie(int movieId) {
        List<String> dayMonthYearOfScheduleByMovie = scheduleRepo.findDistinctDayMonthYearByMovieId(movieId);

        return dayMonthYearOfScheduleByMovie;
    }

    @Override
    public List<ScheduleByDayAndMovieDTO> getAllScheduleByDayAndMovie(int movieId, String startDate) {
        List<Object[]> objects = scheduleRepo.findScheduleByMovieIdAndStartDate(movieId,startDate);

        List<ScheduleByDayAndMovieDTO> scheduleDTOs = new ArrayList<>();
        for (Object[] obj: objects){
                String startTime = (String) obj[0];
                Integer capacity = (Integer) obj[1];
                String nameRoom = (String) obj[2];
                Integer roomId = (Integer) obj[3];
            ScheduleByDayAndMovieDTO scheduleDTO = new ScheduleByDayAndMovieDTO(startTime, capacity, nameRoom,roomId);
            scheduleDTOs.add(scheduleDTO);
        }
        return scheduleDTOs;
    }

    @Override
    public double getPriceBySchedule(String startTime, String startDate, int movieId) throws Exception {
        return scheduleRepo.getPriceBySchedule(startTime,startDate,movieId);
    }

    @Override
    public List<ScheduleDTO> getAllScheduleByMovie(String slugMovie) throws Exception {
        Movie movie = movieRepo.findBySlug(slugMovie);
        int movieId = movie.getId();
        if(movie == null) {
            throw  new DataNotFoundException(MessageKeys.MOVIE_DOES_NOT_EXIST);
        }
        LocalDate today = LocalDate.now();

        // Tạo một mảng các ngày trong tuần, bắt đầu từ thứ 2 đến chủ nhật
        DayOfWeek[] daysOfWeek = {DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY};

        // Lấy index của ngày hiện tại trong mảng daysOfWeek
        int todayIndex = today.getDayOfWeek().getValue() - 1;

        // Số lượng tab sẽ phụ thuộc vào ngày hiện tại
        int numberOfTabs = 7;

        List<ScheduleDTO> scheduleDTOS = new ArrayList<>();
        for (int i = 0; i < numberOfTabs; i++) {
            // Lấy ngày hiện tại cộng thêm số ngày i
            LocalDate date = today.plusDays(i);

            // Lấy ngày trong tuần
            DayOfWeek dayOfWeek = daysOfWeek[(todayIndex + i) % 7];
            String day = dayOfWeek.toString();

            // Format ngày tháng
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String formattedDate = date.format(formatter);

            DateTimeFormatter formatterDayMonth = DateTimeFormatter.ofPattern("dd/MM");
            String formattedDateMonth = date.format(formatterDayMonth);
            //ấy danh sách dịch có phim dựa theo slug truyền vào
            List<Object[]> objects = scheduleRepo.findScheduleByMovieIdAndStartDate(movieId, formattedDate);

            if(!objects.isEmpty()) {
                Set<ScheduleByDayDTO> scheduleByDayDTOS = new HashSet<>();
                for (Object[] obj: objects){
                    String startAt = (String) obj[0];
                    Integer scheduleId = (Integer) obj[1];
                    Integer capacity = (Integer) obj[2];
                    ScheduleByDayDTO scheduleByDTO = new ScheduleByDayDTO(scheduleId,startAt, capacity);
                    scheduleByDayDTOS.add(scheduleByDTO);
                }

                ScheduleDTO scheduleDTO = ScheduleDTO.builder()

                        .day(formattedDate)
                        .scheduleByDayDTOSet(scheduleByDayDTOS)
                        .build();
                scheduleDTOS.add(scheduleDTO);
            }
        }
        return scheduleDTOS;
    }

    public String localDateTimeToString (LocalDateTime localDateTime){
        // Định dạng LocalDateTime thành chuỗi
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDateTime = localDateTime.format(formatter);
        return formattedDateTime;
    }

    @Override
    public List<ScheduleByAdminDTO> getAllScheduleByAdmin() throws Exception {
        List<ScheduleByAdminDTO> scheduleByAdminDTOS = new ArrayList<>();
        for (Schedule schedule:scheduleRepo.findAll()){
            ScheduleByAdminDTO scheduleByAdminDTO = new ScheduleByAdminDTO();
            scheduleByAdminDTO.setCode(schedule.getCode());
            scheduleByAdminDTO.setStartAt(localDateTimeToString(schedule.getStartAt()));
            scheduleByAdminDTO.setEndAt(localDateTimeToString(schedule.getEndAt()));
            scheduleByAdminDTO.setMovie(schedule.getMovie().getName());
            scheduleByAdminDTO.setRoom(schedule.getRoom().getName());
            scheduleByAdminDTO.setPrice(schedule.getPrice());
            scheduleByAdminDTOS.add(scheduleByAdminDTO);
        }
        return scheduleByAdminDTOS;
    }

    public static LocalDateTime stringToLocalDateTime(String time) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime localDateTime = LocalDateTime.parse(time, inputFormatter);

        // Định dạng lại đối tượng LocalDateTime thành "yyyy-MM-dd HH:mm:ss.SSSSSS"
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        String formattedDateTime = localDateTime.format(outputFormatter);

        // Chuyển đổi lại từ chuỗi định dạng "yyyy-MM-dd HH:mm:ss.SSSSSS" thành LocalDateTime
        return LocalDateTime.parse(formattedDateTime, outputFormatter);
    }

    public boolean hasOverlap(LocalDateTime startAt1, LocalDateTime endAt1, LocalDateTime startAt2, LocalDateTime endAt2) {
        return !startAt1.isAfter(endAt2) && !startAt2.isAfter(endAt1);
    }

    @Override
    public Schedule createSchedule(CreateScheduleRequest createScheduleRequest) throws Exception {
        Schedule schedule = scheduleRepo
        LocalDateTime start = stringToLocalDateTime(createScheduleRequest.getStartAt());
        LocalDateTime end = stringToLocalDateTime(createScheduleRequest.getEndTime());
        if(hasOverlap())
        return null;
    }
}
