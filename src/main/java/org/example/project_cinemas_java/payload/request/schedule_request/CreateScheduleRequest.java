package org.example.project_cinemas_java.payload.request.schedule_request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateScheduleRequest {
    private double price;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    private String code;
    private String name;
    private MovieScheduleRequest movie;
    private RoomScheduleRequest room;
}
