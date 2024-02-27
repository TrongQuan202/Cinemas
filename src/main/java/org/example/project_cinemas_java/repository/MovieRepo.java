package org.example.project_cinemas_java.repository;

import org.example.project_cinemas_java.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepo extends JpaRepository<Movie, Integer> {
    boolean existsByImage( String image);

    boolean existsByTrailer(String trailer);

    boolean existsByHerolmage(String heroImage);

    boolean existsByImageAndIdNot(String image, int id);

    boolean existsByHerolmageAndIdNot(String heroImage, int id);

    boolean existsByTrailerAndIdNot(String trailer, int id);

    @Query(nativeQuery = true,
            value = "SELECT m.id, m.name, COUNT(t.id) AS TicketCount " +
                    "FROM cinema.movie m " +
                    "JOIN cinema.schedule s ON m.id = s.movie_id " +
                    "JOIN cinema.ticket t ON s.id = t.schedule_id " +
                    "GROUP BY m.id, m.name " +
                    "ORDER BY TicketCount DESC")
    List< Object[] > findMoviesOrderByTicketCount();

}
