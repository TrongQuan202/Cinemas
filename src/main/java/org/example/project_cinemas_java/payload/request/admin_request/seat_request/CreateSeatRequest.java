package org.example.project_cinemas_java.payload.request.admin_request.seat_request;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateSeatRequest {
    private int number;

    private String line;

    private int seatStatusId;

    private int seatTypeId;

    private int roomId;
}
