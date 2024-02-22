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
    @JoinColumn(name = "movieTypeId" , foreignKey = @ForeignKey(name = "fk_Movie_MovieType"), nullable = false)
    @JsonManagedReference
    private MovieType movietype;

    private String name;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name ="rateId", foreignKey = @ForeignKey(name = "fk_Movie_Rate"), nullable = false)
    @JsonManagedReference
    private Rate rate;

    private String trailer;

    private boolean isActive = true;
}
