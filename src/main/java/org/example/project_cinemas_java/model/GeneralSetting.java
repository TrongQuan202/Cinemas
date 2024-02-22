package org.example.project_cinemas_java.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "generalsetting")
@Builder
public class GeneralSetting extends BaseEntity{
    private LocalDateTime breakTime;

    private int businessHours;

    private LocalDateTime closeTime;

    private double fixedTicketPrice;

    private int percentDay;

    private int percentWeekend;

    private LocalDateTime timeBeginToChange;
}
