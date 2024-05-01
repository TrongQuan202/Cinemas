package org.example.project_cinemas_java.payload.dto.seatdtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SeatCountAndMoneyDTO {
    private Integer seatSelectedCount;
    private Float money;
}

