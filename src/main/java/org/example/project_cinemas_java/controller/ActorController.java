package org.example.project_cinemas_java.controller;

import lombok.RequiredArgsConstructor;
import org.example.project_cinemas_java.exceptions.DataIntegrityViolationException;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.model.Actor;
import org.example.project_cinemas_java.payload.request.actor_request.CreateActorRequest;
import org.example.project_cinemas_java.payload.request.movie_request.ActorRequest;
import org.example.project_cinemas_java.service.implement.ActorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/actor")
@RequiredArgsConstructor
public class ActorController {
    @Autowired
    private ActorService actorService;

    @PostMapping("/create-actor")
    public ResponseEntity<?> createActor(@RequestBody CreateActorRequest createActorRequest){
        try {
            Actor actor = actorService.createActor(createActorRequest);
            return ResponseEntity.ok().body(actor);
        }catch (DataIntegrityViolationException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/get-all-actor-name")
    public ResponseEntity<?> getAllActorName(){
        try {
            List<ActorRequest> actorRequests = actorService.getAllActorName();
            return ResponseEntity.ok().body(actorRequests);
        }catch (DataIntegrityViolationException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
