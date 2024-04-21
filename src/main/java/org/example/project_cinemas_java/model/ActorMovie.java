package org.example.project_cinemas_java.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "actorMovie")
@Builder
public class ActorMovie extends BaseEntity{
    private String movieName;
    private String slugMovie;
    private String imageMovie;
    private String role;

    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "movieId", foreignKey = @ForeignKey(name = "fk_ActorMovie_Movie"))
    @JsonManagedReference
    private Movie movie;

    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "actorId", foreignKey = @ForeignKey(name = "fk_ActorMovie_Actor"))
    @JsonManagedReference
    private Actor actor;

}
