package org.example.project_cinemas_java.service.implement;

import org.example.project_cinemas_java.exceptions.DataIntegrityViolationException;
import org.example.project_cinemas_java.model.Actor;
import org.example.project_cinemas_java.payload.request.actor_request.CreateActorRequest;
import org.example.project_cinemas_java.payload.request.movie_request.ActorRequest;
import org.example.project_cinemas_java.repository.ActorRepo;
import org.example.project_cinemas_java.service.iservice.IActorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ActorService implements IActorService {

    @Autowired
    private ActorRepo actorRepo;
    @Override
    public Actor createActor(CreateActorRequest createActorRequest) throws Exception {
        Actor actorCheck = actorRepo.findBySlug(createActorRequest.getSlug());
        if(actorCheck != null){
            throw new DataIntegrityViolationException("Diễn viên đã tồn tại");
        }
        Actor actor = new Actor();
        actor.setImage(createActorRequest.getImage());
        actor.setHeight(createActorRequest.getHeight());
        actor.setGender(createActorRequest.getGender());
        actor.setName(createActorRequest.getName());
        actor.setSlug(createActorRequest.getSlug());
        actor.setView(0);
        actor.setBirthDay(createActorRequest.getBirthDay());
        actor.setDescription(createActorRequest.getDescription());
        actor.setNationality(createActorRequest.getNationality());
        actorRepo.save(actor);

        return actor;
    }

    @Override
    public List<ActorRequest> getAllActorName() throws Exception {
        List<ActorRequest> actorRequests = new ArrayList<>();
        for (Actor actor: actorRepo.findAll()){
            ActorRequest actorRequest = new ActorRequest();
            actorRequest.setName(actor.getName());
            actorRequests.add(actorRequest);
        }

        return actorRequests;
    }
}
