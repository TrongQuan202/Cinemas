package org.example.project_cinemas_java.service.implement;

import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.model.Cinema;
import org.example.project_cinemas_java.model.Room;
import org.example.project_cinemas_java.payload.dto.roomdtos.RoomScheduleByAdminDTO;
import org.example.project_cinemas_java.payload.request.admin_request.room_request.CreateRoomRequest;
import org.example.project_cinemas_java.payload.request.admin_request.room_request.UpdateRoomRequest;
import org.example.project_cinemas_java.repository.CinemaRepo;
import org.example.project_cinemas_java.repository.RoomRepo;
import org.example.project_cinemas_java.service.iservice.IRoomService;
import org.example.project_cinemas_java.utils.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoomService implements IRoomService {
    @Autowired
    private RoomRepo roomRepo;
    @Autowired
    private CinemaRepo cinemaRepo;

    @Override
    public Room createRoom(CreateRoomRequest createRoomRequest) throws Exception {
        Room room = roomRepo.findByCode(createRoomRequest.getCode());

        if(room != null){
            throw  new DataIntegrityViolationException(MessageKeys.ROOM_ALREADY_EXIST);
        }

        Cinema cinema = cinemaRepo.findById(createRoomRequest.getCinemaId()).orElse(null);

        if (cinema == null){
            throw new DataNotFoundException(MessageKeys.CINEMA_DOES_NOT_EXIST);
        }
        Room newRoom = Room.builder()
                .name(createRoomRequest.getName())
                .code(createRoomRequest.getCode())
                .type(createRoomRequest.getType())
                .cinema(cinema)
                .isActive(true)
                .description(createRoomRequest.getDescription())
                .capacity(createRoomRequest.getCapacity())
                .build();
        roomRepo.save(newRoom);

        return newRoom;
    }

    @Override
    public Room updateRoom(UpdateRoomRequest updateRoomRequest) throws Exception{
        Room room = roomRepo.findById(updateRoomRequest.getRoomId()).orElse(null);
        Cinema cinema = cinemaRepo.findById(updateRoomRequest.getCinemaId()).orElse(null);
        if(cinema ==null){
            throw new DataNotFoundException(MessageKeys.CINEMA_DOES_NOT_EXIST);
        }
        if(room == null){
            throw new DataNotFoundException(MessageKeys.ROOM_DOES_NOT_EXIST);
        }
        if(!roomRepo.findAllByCodeAndIdNot(updateRoomRequest.getCode(),room.getId()).isEmpty()){
            throw new DataIntegrityViolationException(MessageKeys.CODE_ALREADY_EXIST);
        }
        room.setCinema(cinema);
        room.setCapacity(updateRoomRequest.getCapacity());
        room.setName(updateRoomRequest.getName());
        room.setDescription(updateRoomRequest.getDescription());
        room.setCode(updateRoomRequest.getCode());
        room.setName(updateRoomRequest.getName());
        room.setType(updateRoomRequest.getType());

        roomRepo.save(room);
        return room;
    }

    @Override
    public List<RoomScheduleByAdminDTO> getAllRoomScheduleByAdmin() {
        List<RoomScheduleByAdminDTO> roomScheduleByAdminDTOS = new ArrayList<>();
        for (Room room: roomRepo.findAll()){
            RoomScheduleByAdminDTO roomScheduleByAdminDTO = new RoomScheduleByAdminDTO();
            roomScheduleByAdminDTO.setRoomName(room.getName());
            roomScheduleByAdminDTO.setId(room.getId());
            roomScheduleByAdminDTOS.add(roomScheduleByAdminDTO);
        }
        return roomScheduleByAdminDTOS;
    }
}
