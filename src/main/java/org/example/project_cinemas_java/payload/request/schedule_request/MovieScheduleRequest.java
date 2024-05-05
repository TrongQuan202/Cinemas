package org.example.project_cinemas_java.payload.request.schedule_request;

import lombok.Data;

@Data
public class MovieScheduleRequest {
    private int movieId;
    private String movieName;
}
