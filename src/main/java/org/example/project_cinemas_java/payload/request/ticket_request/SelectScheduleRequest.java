package org.example.project_cinemas_java.payload.request.ticket_request;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SelectScheduleRequest {
    private String day;
    private String time;

}
