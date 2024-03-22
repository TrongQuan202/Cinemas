package org.example.project_cinemas_java.payload.dto.scheduledtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleByDayAndMovieDTO {
    private String startTime;
    private Integer capacity;
    private String nameRoom;
    private int roomId;
}
