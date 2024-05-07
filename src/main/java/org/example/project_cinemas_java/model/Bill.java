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
@Table(name = "bill")
@Builder
public class Bill extends BaseEntity {
    private double totalMoney;

    private String tradingCode;

    private LocalDateTime createTime;

    private int month;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "customerId", foreignKey = @ForeignKey(name = "fk_Bill_User"))
    @JsonManagedReference
    private User user;

    private String name;

    private LocalDateTime updateTime;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "promotionId", foreignKey = @ForeignKey(name = "fk_Bill_Promotion"))
    @JsonManagedReference
    private Promotion promotion;

    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "billStatusId", foreignKey = @ForeignKey(name = "fk_Bill_BillStatus"))
    @JsonManagedReference
    private BillStatus billstatus;

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    private Set<BillFood> billFoods;

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    private Set<BillTicket> billTickets;

    private boolean isActive = true;




}
