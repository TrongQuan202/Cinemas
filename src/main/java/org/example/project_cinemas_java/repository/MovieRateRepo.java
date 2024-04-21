package org.example.project_cinemas_java.repository;

import org.example.project_cinemas_java.model.Movie;
import org.example.project_cinemas_java.model.MovieRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRateRepo extends JpaRepository<MovieRate, Integer> {
    List<MovieRate> findAllByMovie(Movie movie);

}
