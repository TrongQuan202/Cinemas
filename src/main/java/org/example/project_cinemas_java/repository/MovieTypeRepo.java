package org.example.project_cinemas_java.repository;

import org.example.project_cinemas_java.model.Movie;
import org.example.project_cinemas_java.model.MovieType;
import org.example.project_cinemas_java.model.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface MovieTypeRepo extends JpaRepository<MovieType, Integer> {
    boolean existsByMovieAndType(Movie movie, Type type);

    Set<MovieType> findAllByMovie(Movie movie);
}
