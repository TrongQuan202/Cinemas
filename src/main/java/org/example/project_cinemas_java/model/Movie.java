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
    private String name;

    private int movieDuration;

    private String slug;

    private LocalDateTime endTime;

    private LocalDateTime premiereDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String director;

    private String image;

    private String herolmage;
    private String imageSuggest;

    private String language;

    private String trailer;

    private boolean isActive = true;

    private boolean isUpcoming;

    public boolean isUpcoming() {
        return isUpcoming;
    }

    public void setUpcoming(boolean upcoming) {
        isUpcoming = upcoming;
    }

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    private Set<MovieType> movieTypes;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    private Set<MovieRate> movieRates;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "cinemaId", foreignKey = @ForeignKey(name = "fk_Movie_Cinema"))
    @JsonManagedReference
    private Cinema cinema;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    private Set<Schedule> schedules;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    private Set<ActorMovie> actorMovies;


}
