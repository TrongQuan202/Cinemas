package org.example.project_cinemas_java.payload.dto.seatdtos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SeatTotalMoneyDTO {
    private List<SeatSelectedDTO> seatSelectedDTOS;
    private Float totalMoney;
}
