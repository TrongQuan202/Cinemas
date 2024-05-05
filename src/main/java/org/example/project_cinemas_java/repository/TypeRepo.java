package org.example.project_cinemas_java.repository;

import org.example.project_cinemas_java.model.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeRepo extends JpaRepository<Type, Integer> {
    boolean existsByMovieTypeName(String movieTypeName);

    Type findByMovieTypeName(String name);
}
