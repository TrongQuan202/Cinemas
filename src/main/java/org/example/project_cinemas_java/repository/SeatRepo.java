package org.example.project_cinemas_java.repository;

import org.example.project_cinemas_java.model.Room;
import org.example.project_cinemas_java.model.Schedule;
import org.example.project_cinemas_java.model.Seat;
import org.example.project_cinemas_java.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface SeatRepo extends JpaRepository<Seat, Integer> {
    boolean deleteAllByRoom(Room room);
    List<Seat> findAllByRoom(Room room);

    boolean existsByLineAndNumberAndIdNot(String line, int number, int seatId);
    List<Seat> findAllByNumberAndLine(int number, String line);

    List<Seat> findAllByRoomAndSchedule(Room room, Schedule schedule);



//    List<Seat> findAllByTickets(Set<Ticket> tickets);
//    @Query(nativeQuery = true,
//           value = "SELECT s FROM cinemalts.seat s WHERE s.ticket IN :tickets")
//    List<Seat> findAllByTickets(Set<Ticket> tickets);
}
