package org.example.project_cinemas_java.payload.request.schedule_request;

import lombok.Data;

@Data
public class RoomScheduleRequest {
    private int roomId;
    private String roomName;
}
