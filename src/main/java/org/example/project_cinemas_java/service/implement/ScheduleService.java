package org.example.project_cinemas_java.service.implement;

import org.example.project_cinemas_java.payload.dto.scheduledtos.DayMonthYearOfScheduleByMovieDTO;
import org.example.project_cinemas_java.payload.dto.scheduledtos.ScheduleByDayAndMovieDTO;
import org.example.project_cinemas_java.repository.ScheduleRepo;
import org.example.project_cinemas_java.service.iservice.IScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class ScheduleService implements IScheduleService {
    @Autowired
    private ScheduleRepo scheduleRepo;

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
}
