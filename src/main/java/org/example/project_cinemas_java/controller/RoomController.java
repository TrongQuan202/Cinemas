package org.example.project_cinemas_java.controller;

import lombok.RequiredArgsConstructor;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.model.Room;
import org.example.project_cinemas_java.payload.dto.roomdtos.RoomScheduleByAdminDTO;
import org.example.project_cinemas_java.payload.request.admin_request.room_request.CreateRoomRequest;
import org.example.project_cinemas_java.payload.request.admin_request.room_request.UpdateRoomRequest;
import org.example.project_cinemas_java.service.implement.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/room")
@RequiredArgsConstructor
public class RoomController {
    @Autowired
    private RoomService roomService;

    @PostMapping("/create-room")
    public ResponseEntity<?> createRoom(@RequestBody CreateRoomRequest createRoomRequest){
        try {
            Room room = roomService.createRoom(createRoomRequest);
            return ResponseEntity.ok().body(room);
        }catch (DataIntegrityViolationException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/update-room")
    public ResponseEntity<?> updateRoom(@RequestBody UpdateRoomRequest updateRoomRequest){
        try {
            Room room = roomService.updateRoom(updateRoomRequest);
            return ResponseEntity.ok().body(room);
        }catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }catch (DataIntegrityViolationException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/get-all-room-schedule-by-admin")
    public ResponseEntity<?> getAllRoomScheduleByAdmin() {
        List<RoomScheduleByAdminDTO> roomScheduleByAdminDTOS = roomService.getAllRoomScheduleByAdmin();
        return ResponseEntity.ok().body(roomScheduleByAdminDTOS);

    }

}
