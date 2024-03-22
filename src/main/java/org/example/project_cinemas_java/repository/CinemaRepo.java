package org.example.project_cinemas_java.repository;

import org.example.project_cinemas_java.model.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CinemaRepo extends JpaRepository<Cinema, Integer> {

    @Query("SELECT c.nameOfCinema FROM Cinema c WHERE c.address LIKE %:address%")
    List<String> findByAddressContaining(String address);

    Cinema findByCode(String code);
    boolean existsByAddress (String address);

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
}
