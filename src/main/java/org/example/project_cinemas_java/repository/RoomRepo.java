package org.example.project_cinemas_java.repository;

import org.example.project_cinemas_java.model.Cinema;
import org.example.project_cinemas_java.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepo extends JpaRepository<Room, Integer> {
    void deleteAllByCinema(Cinema cinema);

    boolean existsByCode(String code);

    List<Room> findAllByCodeAndIdNot(String code, int id);

    boolean existsByName(String name);
    Room findByCode(String code);
    List<Room> findAllByCinema(Cinema cinema);

    List<Room> findAllByCode(String code);
}
