package org.example.project_cinemas_java.service.iservice;

import org.example.project_cinemas_java.payload.dto.scheduledtos.DayMonthYearOfScheduleByMovieDTO;
import org.example.project_cinemas_java.payload.dto.scheduledtos.ScheduleByDayAndMovieDTO;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IScheduleService {
    List<String> getAllDayMonthYearOfScheduleByMovie (int movieId);

    List<ScheduleByDayAndMovieDTO> getAllScheduleByDayAndMovie(int movieId, String startDate);

    double getPriceBySchedule (String startTime,String startDate,int movieId) throws Exception;
}
