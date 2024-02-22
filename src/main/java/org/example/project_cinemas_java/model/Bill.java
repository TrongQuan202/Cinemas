package org.example.project_cinemas_java.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bill")
@Builder
public class Bill extends BaseEntity {
    private double totalMoney;

    private String tradingCode;

    private LocalDate createTime;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "customerId", foreignKey = @ForeignKey(name = "fk_Bill_User"), nullable = false)
    @JsonManagedReference
    private User user;

    private String name;

    private LocalDate updateTime;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "promotionId", foreignKey = @ForeignKey(name = "fk_Bill_Promotion"), nullable = false)
    @JsonManagedReference
    private Promotion promotion;

    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "billStatusId", foreignKey = @ForeignKey(name = "fk_Bill_BillStatus"), nullable = false)
    @JsonManagedReference
    private BillStatus billstatus;

    private boolean isActive = true;




}
