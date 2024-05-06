package org.example.project_cinemas_java.service.iservice;

import org.example.project_cinemas_java.model.Actor;
import org.example.project_cinemas_java.payload.request.actor_request.CreateActorRequest;
import org.example.project_cinemas_java.payload.request.movie_request.ActorRequest;

import java.util.List;

public interface IActorService {
    Actor createActor(CreateActorRequest createActorRequest)throws Exception;
    List<ActorRequest> getAllActorName() throws Exception;
}
