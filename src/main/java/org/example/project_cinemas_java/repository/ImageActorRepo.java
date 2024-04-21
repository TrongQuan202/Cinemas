package org.example.project_cinemas_java.repository;

import org.example.project_cinemas_java.model.ImageActor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageActorRepo extends JpaRepository<ImageActor, Integer> {
}
