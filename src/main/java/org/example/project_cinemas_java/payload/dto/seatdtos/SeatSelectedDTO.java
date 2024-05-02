package org.example.project_cinemas_java.payload.dto.seatdtos;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SeatSelectedDTO {
    private Integer seatType;
    private Integer seatSelectedCount;
    private Float price;
    private double totalMoney;
    private Set<String> seatSelected;
}
