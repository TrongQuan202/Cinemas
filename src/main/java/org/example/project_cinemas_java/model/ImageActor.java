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
@Table(name = "imageActor")
@Builder
public class ImageActor extends BaseEntity{
    private String imageActor;

    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "actorId", foreignKey = @ForeignKey(name = "fk_ImageActor_Actor"))
    @JsonManagedReference
    private Actor actor;
}
