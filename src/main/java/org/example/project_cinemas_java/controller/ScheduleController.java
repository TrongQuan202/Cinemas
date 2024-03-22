package org.example.project_cinemas_java.controller;

import lombok.RequiredArgsConstructor;
import org.example.project_cinemas_java.service.implement.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/schedule")
@RequiredArgsConstructor
public class ScheduleController {
    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/get-schedule-by-movie")
    public ResponseEntity<?> getAllDayMonthYearOfScheduleByMovie(@RequestParam int movieId){
        return ResponseEntity.ok().body(scheduleService.getAllDayMonthYearOfScheduleByMovie(movieId));
    }
    @GetMapping("/get-schedule-by-day-and-movie")
    public ResponseEntity<?> getAllScheduleByDayAndMovie(@RequestParam int movieId, String startDate){
        return ResponseEntity.ok().body(scheduleService.getAllScheduleByDayAndMovie(movieId,startDate));
    }
    @GetMapping("/get-price-by-schedule")
    public ResponseEntity<?> getPriceBySchedule(@RequestParam String startTime, String startDate, int movieId){
        try {
            double priceBySchedule = scheduleService.getPriceBySchedule(startTime,startDate,movieId);
            return ResponseEntity.ok().body(priceBySchedule);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
