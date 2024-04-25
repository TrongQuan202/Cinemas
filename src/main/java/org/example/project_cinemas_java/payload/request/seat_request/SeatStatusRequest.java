package org.example.project_cinemas_java.payload.request.seat_request;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SeatStatusRequest {
    private Integer seatId;
    private Integer userId;
    private Integer seatStatus;
    private Integer schedule;
}
