package org.example.project_cinemas_java.payload.request.seat_request;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    public static SeatStatusRequest fromJson(String json) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, SeatStatusRequest.class);
    }
}
