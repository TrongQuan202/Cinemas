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
@Table(name = "movieType")
@Builder
public class MovieType extends BaseEntity{

    private String movieTypeName;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "typeId" , foreignKey = @ForeignKey(name = "fk_MovieType_Type"))
    @JsonManagedReference
    private Type type;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "movieId" , foreignKey = @ForeignKey(name = "fk_MovieType_Movie"))
    @JsonManagedReference
    private Movie movie;

    private boolean isActive;
}
