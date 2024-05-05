package org.example.project_cinemas_java.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.payload.dto.scheduledtos.ScheduleByAdminDTO;
import org.example.project_cinemas_java.payload.dto.scheduledtos.ScheduleDTO;
import org.example.project_cinemas_java.service.implement.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/schedule")
@RequiredArgsConstructor
public class ScheduleController {
    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/get-schedule-by-movie")
    public ResponseEntity<?> getAllDayMonthYearOfScheduleByMovie(@RequestParam int movieId) {
        return ResponseEntity.ok().body(scheduleService.getAllDayMonthYearOfScheduleByMovie(movieId));
    }

    @GetMapping("/get-schedule-by-day-and-movie")
    public ResponseEntity<?> getAllScheduleByDayAndMovie(@RequestParam int movieId, String startDate) {
        return ResponseEntity.ok().body(scheduleService.getAllScheduleByDayAndMovie(movieId, startDate));
    }

    @GetMapping("/get-price-by-schedule")
    public ResponseEntity<?> getPriceBySchedule(@RequestParam String startTime, String startDate, int movieId) {
        try {
            double priceBySchedule = scheduleService.getPriceBySchedule(startTime, startDate, movieId);
            return ResponseEntity.ok().body(priceBySchedule);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/get-all-schedule-by-movie")
    public ResponseEntity<?> getAllScheduleByMovie(@RequestParam String slug) {
        try {
            List<ScheduleDTO> scheduleDTOS= scheduleService.getAllScheduleByMovie(slug);
            return ResponseEntity.ok().body(scheduleDTOS);
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/get-all-schedule-by-admin")
    public ResponseEntity<?> getAllScheduleByAdmin() {
        try {
            List<ScheduleByAdminDTO> scheduleDTOS= scheduleService.getAllScheduleByAdmin();
            return ResponseEntity.ok().body(scheduleDTOS);
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }




}
