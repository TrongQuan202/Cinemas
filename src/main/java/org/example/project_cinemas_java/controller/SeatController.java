package org.example.project_cinemas_java.controller;

import lombok.RequiredArgsConstructor;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.model.Seat;
import org.example.project_cinemas_java.payload.dto.seatdtos.SeatsByRoomDTO;
import org.example.project_cinemas_java.payload.request.admin_request.seat_request.CreateSeatRequest;
import org.example.project_cinemas_java.payload.request.admin_request.seat_request.UpdateSeatRequest;
import org.example.project_cinemas_java.service.implement.SeatService;
import org.example.project_cinemas_java.service.iservice.ISeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/seat")
@RequiredArgsConstructor
public class SeatController {
    @Autowired
    private SeatService seatService;

    @PostMapping("/create-seat")
    public ResponseEntity<?> createSeat(@RequestBody CreateSeatRequest createSeatRequest){
        try {
            Seat seat = seatService.createSeat(createSeatRequest);
            return ResponseEntity.ok().body(seat);
        }catch (DataIntegrityViolationException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/update-seat")
    public ResponseEntity<?> updateSeat(@RequestBody UpdateSeatRequest updateSeatRequest){
        try {
            Seat seat = seatService.updateSeat(updateSeatRequest);
            return ResponseEntity.ok().body(seat);
        }catch (DataIntegrityViolationException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping("/get-all-seat-by-room")
    public ResponseEntity<?> getAllSeatByRoom(@RequestParam int roomId){
        try {
            List<SeatsByRoomDTO> seats = seatService.getAllSeatByRoom(roomId);
            return ResponseEntity.ok().body(seats);
        }catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @CrossOrigin(origins = "http://localhost:8080")
    @PutMapping("/update-seat-status")
    public ResponseEntity<?> updateStatusSeatsByScheduleAndRoom(@RequestParam String dayMonthYear,
                                                                String startTime, int movieId, int roomId,
                                                                int seatStatus, int seatId,String email){
        try {
            seatService.updateStatusSeatsByScheduleAndRoom(dayMonthYear,startTime,movieId,roomId,seatStatus,seatId,email);
            return ResponseEntity.ok().body("update sucess");
        }catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @PutMapping("/reset-seat-status")
    public ResponseEntity<?> resetSeats(@RequestParam String dayMonthYear,
                                        String startTime, int movieId, int roomId){
        try {

            return ResponseEntity.ok().body(seatService.resetSeats(dayMonthYear,startTime,movieId,roomId));
        }catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/reset-seat-by-user")
    public ResponseEntity<?> resetSeatsByUser(@RequestParam String dayMonthYear,
                                        String startTime, int movieId, int roomId, String token){
        try {
            return ResponseEntity.ok().body(seatService.resetSeatByUser(dayMonthYear,startTime,movieId,roomId, token));
        }catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
