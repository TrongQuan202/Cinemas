package org.example.project_cinemas_java.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "promotion")
@Builder
public class Promotion extends BaseEntity {
    private int percent;

    private int quantity;

    private String type;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String description;

    private String name;

    private boolean isActive = true;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "rankCustomerId", foreignKey = @ForeignKey(name = "fk_Promotion_RankCustomer"), nullable = false)
    @JsonManagedReference
    private RankCustomer rankcustomer;

    @OneToMany(mappedBy = "promotion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    private Set<Bill> bills;
}
