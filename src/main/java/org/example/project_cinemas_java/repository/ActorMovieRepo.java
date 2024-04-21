package org.example.project_cinemas_java.repository;

import org.example.project_cinemas_java.model.Actor;
import org.example.project_cinemas_java.model.ActorMovie;
import org.example.project_cinemas_java.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ActorMovieRepo extends JpaRepository<ActorMovie, Integer> {
    Set<ActorMovie> findAllByMovie(Movie movie);
}
