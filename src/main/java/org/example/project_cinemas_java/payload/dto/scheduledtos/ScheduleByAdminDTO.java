package org.example.project_cinemas_java.payload.dto.scheduledtos;

import lombok.*;
import org.hibernate.query.sql.internal.ParameterRecognizerImpl;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleByAdminDTO {
    private String code;
    private String startAt;
    private String endAt;
    private double price;
    private String movie;
    private String room;

}
