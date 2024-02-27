package org.example.project_cinemas_java.controller;

import lombok.RequiredArgsConstructor;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.model.Cinema;
import org.example.project_cinemas_java.payload.request.admin_request.cinema_request.CreateCinemaRequest;
import org.example.project_cinemas_java.payload.request.admin_request.cinema_request.UpdateCinemaRequest;
import org.example.project_cinemas_java.service.implement.CinemaService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/cinema")
@RequiredArgsConstructor
public class CinemaController {

    private final CinemaService cinemaService;

    @PostMapping("/create-cinema")
    public ResponseEntity<?> createCinema(@RequestBody CreateCinemaRequest  createCinemaRequest){
        try {
            Cinema newCinema = cinemaService.createCinema(createCinemaRequest);
            return ResponseEntity.ok().body(newCinema);
        } catch (DataIntegrityViolationException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @PutMapping("/update-cinema")
    public ResponseEntity<?> updateCinema(@RequestBody UpdateCinemaRequest updateCinemaRequest){
        try {
            Cinema cinema = cinemaService.updateCinema(updateCinemaRequest);
            return ResponseEntity.ok().body(cinema);
        }catch (DataIntegrityViolationException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @DeleteMapping("/delete-cinema")
    public ResponseEntity<?> deleteCinema(@RequestParam int cinemaId){
        try {
            String deleteCinemaString = cinemaService.deleteCinema(cinemaId);
            return ResponseEntity.ok().body(deleteCinemaString);
        }catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


}
