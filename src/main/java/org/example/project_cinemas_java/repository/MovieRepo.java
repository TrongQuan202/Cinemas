package org.example.project_cinemas_java.repository;

import org.example.project_cinemas_java.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query(nativeQuery = true, value = "SELECT m.id, m.name, m.image, m.movie_duration, m.trailer, m.movie_type_name, m.description, m.director, m.language, m.premiere_date " +
            "FROM (" +
            "    SELECT DISTINCT m.id, m.name, m.image, m.movie_duration, m.trailer, mt.movie_type_name, m.description, m.director, m.language, m.premiere_date " +
            "    FROM cinemalts.cinema c " +
            "    INNER JOIN cinemalts.room r ON c.id = r.cinema_id " +
            "    INNER JOIN cinemalts.schedule s ON r.id = s.room_id " +
            "    INNER JOIN cinemalts.movie m ON s.movie_id = m.id " +
            "    INNER JOIN cinemalts.movie_type mt ON m.movie_type_id = mt.id " +
            "    WHERE c.name_of_cinema = :nameOfCinema" +
            ") AS m")
    List<Object[]> findMoviesOrderByTicketCount(@Param("nameOfCinema") String  nameOfCinema);



}
