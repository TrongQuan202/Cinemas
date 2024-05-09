package org.example.project_cinemas_java.repository;

import jakarta.persistence.Tuple;
import org.example.project_cinemas_java.model.Cinema;
import org.example.project_cinemas_java.payload.dto.cinemadtos.RevenueCinemaDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CinemaRepo extends JpaRepository<Cinema, Integer> {

    @Query("SELECT c.nameOfCinema FROM Cinema c WHERE c.address LIKE %:address%")
    List<String> findByAddressContaining(String address);

    Cinema findByCode(String code);
    boolean existsByAddress (String address);
    Cinema findBynameOfCinema(String nameOfCinema);
    boolean existsByNameOfCinema(String nameOfCinema);

    boolean existsByCode (String code);

    List<Cinema> findAllByAddressAndIdNot(String address, int id);

    List<Cinema> findAllByNameOfCinemaAndIdNot(String nameOfCode, int id);

    List<Cinema> findAllByCodeAndIdNot(String code, int id);
//
//    boolean existsByAddressAndNameOfCinemaAndCode(String address, String nameOfCinema, String code);


    List<Cinema> findAllByAddress(String address);

    List<Cinema> findAllByNameOfCinema(String nameOfCiname);

    List<Cinema> findAllByCode(String code);

    @Query(value = "SELECT DATE_FORMAT(b.create_time, '%Y-%m') AS months, SUM(b.total_money) AS revenue " +
            "FROM cinemalts.bill b " +
            "GROUP BY DATE_FORMAT(b.create_time, '%Y-%m') " +
            "ORDER BY months DESC " +
            "LIMIT 6", nativeQuery = true)
    List<Tuple> findRevenueByMonth();
}
