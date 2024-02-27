package org.example.project_cinemas_java.payload.request.admin_request.room_request;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateRoomRequest {
    private int roomId;
    private int capacity;

    private int type;

    private String description;

    private String code;

    private String name;

    private int cinemaId;
}
