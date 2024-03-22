package org.example.project_cinemas_java.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "movie")
@Builder
public class Movie extends BaseEntity {
    private int movieDuration;

    private LocalDateTime endTime;

    private LocalDateTime premiereDate;

    private String description;

    private String director;

    private String image;

    private String herolmage;

    private String language;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "movieTypeId" , foreignKey = @ForeignKey(name = "fk_Movie_MovieType"))
    @JsonManagedReference
    private MovieType movietype;

    private String name;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name ="rateId", foreignKey = @ForeignKey(name = "fk_Movie_Rate"))
    @JsonManagedReference
    private Rate rate;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    private Set<Schedule> schedules;

    private String trailer;

    private boolean isActive = true;
}
