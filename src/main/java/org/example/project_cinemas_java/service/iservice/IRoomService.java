package org.example.project_cinemas_java.service.iservice;

import org.example.project_cinemas_java.model.Room;
import org.example.project_cinemas_java.payload.dto.roomdtos.RoomScheduleByAdminDTO;
import org.example.project_cinemas_java.payload.request.admin_request.room_request.CreateRoomRequest;
import org.example.project_cinemas_java.payload.request.admin_request.room_request.UpdateRoomRequest;

import java.util.List;

public interface IRoomService {

    Room createRoom(CreateRoomRequest createRoomRequest)throws Exception;

    Room updateRoom(UpdateRoomRequest updateRoomRequest) throws Exception;

    List<RoomScheduleByAdminDTO> getAllRoomScheduleByAdmin();
}
