package org.example.project_cinemas_java.payload.request.admin_request.schedule_request;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateScheduleRequest {
    private String code;
    private String endTime;
    private String startAt;
    private String name;
    private int movie;
    private int room;
    private String price;
}
