package org.example.project_cinemas_java.payload.dto.seatdtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SeatsByRoomDTO {
    private Integer id;
    private Integer userId;
    private Integer scheduleId;
    private String seatLine;
    private Integer seatNumber;
    private Integer seatStatus;
    private Integer seatType;
}
