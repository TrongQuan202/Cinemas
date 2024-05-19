package org.example.project_cinemas_java.service.iservice;

import org.example.project_cinemas_java.model.Schedule;
import org.example.project_cinemas_java.payload.dto.scheduledtos.ScheduleByAdminDTO;
import org.example.project_cinemas_java.payload.dto.scheduledtos.ScheduleByDayAndMovieDTO;
import org.example.project_cinemas_java.payload.dto.scheduledtos.ScheduleDTO;
import org.example.project_cinemas_java.payload.request.DeleteByTimeRequest;
import org.example.project_cinemas_java.payload.request.admin_request.schedule_request.CreateScheduleRequest;

import java.util.List;

public interface IScheduleService {
    List<String> getAllDayMonthYearOfScheduleByMovie (int movieId);

    List<ScheduleByDayAndMovieDTO> getAllScheduleByDayAndMovie(int movieId, String startDate);

    double getPriceBySchedule (String startTime,String startDate,int movieId) throws Exception;

    List<ScheduleDTO> getAllScheduleByMovie(String slugMovie) throws Exception;

    List<ScheduleByAdminDTO> getAllScheduleByAdmin() throws Exception;

    Schedule createSchedule(CreateScheduleRequest createScheduleRequest) throws Exception;

    void deleteScheduleByAdmin(DeleteByTimeRequest deleteByTimeRequest) throws Exception;

}
