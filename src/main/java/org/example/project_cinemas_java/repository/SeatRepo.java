package org.example.project_cinemas_java.repository;

import org.example.project_cinemas_java.model.Room;
import org.example.project_cinemas_java.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepo extends JpaRepository<Seat, Integer> {
    boolean deleteAllByRoom(Room room);
    List<Seat> findAllByRoom(Room room);

    boolean existsByLineAndNumberAndIdNot(String line, int number, int seatId);
    List<Seat> findAllByNumberAndLine(int number, String line);
}
