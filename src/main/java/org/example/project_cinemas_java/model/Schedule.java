package org.example.project_cinemas_java.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "schedule")
@Builder
public class Schedule extends BaseEntity{
    private double price;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    private String code;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "movieId", foreignKey = @ForeignKey(name = "fk_Schedule_Movie"), nullable = false)
    @JsonManagedReference
    private Movie movie;

    private String name;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "roomId", foreignKey = @ForeignKey(name = "fk_Schedule_Room"), nullable = false)
    @JsonManagedReference
    private Room room;

    private boolean isActive = true;
}
