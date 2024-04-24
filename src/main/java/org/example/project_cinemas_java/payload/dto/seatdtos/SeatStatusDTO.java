package org.example.project_cinemas_java.payload.dto.seatdtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SeatStatusDTO {
    private Integer user;
    private Integer seatStatus;
}
