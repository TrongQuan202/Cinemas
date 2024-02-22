package org.example.project_cinemas_java.repository;

import org.example.project_cinemas_java.model.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CinemaRepo extends JpaRepository<Cinema, Integer> {
}
