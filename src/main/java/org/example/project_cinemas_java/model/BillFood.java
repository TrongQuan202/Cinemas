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
@Table(name = "billfood")
@Builder
public class BillFood extends BaseEntity{
    private int quantity;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "billId", foreignKey = @ForeignKey(name = "fk_BillFood_Bill"), nullable = false)
    @JsonManagedReference
    private Bill bill;

    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "foodId", foreignKey = @ForeignKey(name = "fk_BillFood_Food"), nullable = false)
    @JsonManagedReference
    private Food food;

}
