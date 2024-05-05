package org.example.project_cinemas_java.service.iservice;

import org.example.project_cinemas_java.model.Actor;
import org.example.project_cinemas_java.payload.request.actor_request.CreateActorRequest;

public interface IActorService {
    Actor createActor(CreateActorRequest createActorRequest)throws Exception;
}
