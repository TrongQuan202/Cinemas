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
@Table(name = "movieRate")
@Builder
public class MovieRate extends BaseEntity{
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name ="rateId", foreignKey = @ForeignKey(name = "fk_MovieRate_Rate"))
    @JsonManagedReference
    private Rate rate;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name ="MovieId", foreignKey = @ForeignKey(name = "fk_MovieRate_Movie"))
    @JsonManagedReference
    private Movie movie;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name ="userId", foreignKey = @ForeignKey(name = "fk_MovieRate_User"))
    @JsonManagedReference
    private User user;
}
