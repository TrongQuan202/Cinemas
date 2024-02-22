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
@Table(name = "ticket")
@Builder
public class Ticket extends BaseEntity{
    private String code;

    private double priceTicket;

    private boolean isActive =true;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "scheduleId", foreignKey = @ForeignKey(name = "fk_Ticket_Schedule"), nullable = false)
    @JsonManagedReference
    private Schedule schedule;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "seatId", foreignKey = @ForeignKey(name = "fk_Ticket_Seat"), nullable = false)
    @JsonManagedReference
    private Seat seat;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    private Set<BillTicket> billTicketSet;


}
