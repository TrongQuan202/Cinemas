package org.example.project_cinemas_java.payload.dto.billdtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HistoryBillByUserDTO {
    private String billCode;
    private String movieName;
    private String nameOfCinema;
    private String schedule;
    private String seatSelectedAndTotalMoney;
    private String comboFood;
    private String dateBooking;

}
