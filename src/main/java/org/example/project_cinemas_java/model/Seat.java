package org.example.project_cinemas_java.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "seat")
@Builder
public class Seat extends BaseEntity{
    private int number;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "seatStatusId", foreignKey = @ForeignKey(name = "fk_Seat_SeatStatus"), nullable = false)
    @JsonManagedReference
    private SeatStatus seatsStatus;

    private String line;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "roomId", foreignKey = @ForeignKey(name = "fk_Seat_Room"), nullable = false)
    @JsonManagedReference
    private Room room;

    private boolean isActive = true;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "seatTypeId", foreignKey = @ForeignKey(name = "fk_Seat_SeatType"), nullable = false)
    @JsonManagedReference
    private SeatType seatType;

    @OneToMany(mappedBy = "seat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    private Set<Ticket> tickets;

}
