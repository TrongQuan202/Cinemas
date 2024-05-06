package org.example.project_cinemas_java.payload.dto.roomdtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomScheduleByAdminDTO {

    private int id;
    private String roomName;
}
