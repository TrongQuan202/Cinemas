package org.example.project_cinemas_java.payload.dto.billdtos;

import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BillDTO {


    private String tradingCode;
    private String movieName;
    private String nameOfCinema;
    private String nameRoom;
    private String startAtSchedule;
    private Set<String> seats;

    private double totalMoney;


    private LocalDate createTime;

}
