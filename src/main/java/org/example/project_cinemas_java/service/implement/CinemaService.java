package org.example.project_cinemas_java.service.implement;

import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.model.Cinema;
import org.example.project_cinemas_java.model.Room;
import org.example.project_cinemas_java.payload.request.admin_request.cinema_request.CreateCinemaRequest;
import org.example.project_cinemas_java.payload.request.admin_request.cinema_request.UpdateCinemaRequest;
import org.example.project_cinemas_java.repository.CinemaRepo;
import org.example.project_cinemas_java.repository.RoomRepo;
import org.example.project_cinemas_java.repository.ScheduleRepo;
import org.example.project_cinemas_java.repository.SeatRepo;
import org.example.project_cinemas_java.service.iservice.ICinemaService;
import org.example.project_cinemas_java.utils.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//@Transactional
@Service
public class CinemaService implements ICinemaService {
    @Autowired
    private CinemaRepo cinemaRepo;

    @Autowired
    private RoomRepo roomRepo;
    @Autowired
    private SeatRepo seatRepo;
    @Autowired
    private ScheduleRepo scheduleRepo;

    @Override
    public Cinema createCinema(CreateCinemaRequest createCinemaRequest) throws Exception {
        Cinema existingCinema = cinemaRepo.findByCode(createCinemaRequest.getCode());
        if(existingCinema != null){
            throw new DataIntegrityViolationException(MessageKeys.CINEMA_ALREADY_EXISTS);
        }
        if(cinemaRepo.existsByAddress(createCinemaRequest.getAddress())){
            throw new DataIntegrityViolationException(MessageKeys.ADDRESS_ALREADY_EXIST);
        }
        if(cinemaRepo.existsByNameOfCinema(createCinemaRequest.getNameOfCinema())){
            throw new DataIntegrityViolationException(MessageKeys.NAME_CINEMA_ALREADY_EXIST);
        }

        Cinema newCinema = Cinema.builder()
                .nameOfCinema(createCinemaRequest.getNameOfCinema())
                .address(createCinemaRequest.getAddress())
                .isActive(true)
                .code(createCinemaRequest.getCode())
                .description(createCinemaRequest.getDescription())
                .build();
        cinemaRepo.save(newCinema);
        return newCinema;
    }
//    private boolean isUniqueAddress (CreateCinemaRequest createCinemaRequest){
//        List<Cinema> cinemasWithSameAddress = cinemaRepo.findByAddressAndCodeNot(createCinemaRequest.getAddress(), createCinemaRequest.getCode());
//        if(!cinemasWithSameAddress.isEmpty()){
//            return false;
//        }
//        return true;
//    }
//
//    private boolean isUniqueNameOfCinema (CreateCinemaRequest createCinemaRequest){
//        List<Cinema> cinemasWithSameNameOfCinema = cinemaRepo.findByNameOfCinemaAndCodeNot(createCinemaRequest.getNameOfCinema(),createCinemaRequest.getCode());
//        if(!cinemasWithSameNameOfCinema.isEmpty()){
//            return false;
//        }
//        return true;
//    }


    @Override
    public Cinema updateCinema(UpdateCinemaRequest updateCinemaRequest) throws Exception {
        String address = updateCinemaRequest.getAddress();
        String nameOfCinema = updateCinemaRequest.getNameOfCinema();
        String code = updateCinemaRequest.getCode();;
        Cinema cinema = cinemaRepo.findById(updateCinemaRequest.getCinemaId()).orElse(null);
        if(cinema == null){
            throw new DataNotFoundException(MessageKeys.CINEMA_DOES_NOT_EXIST);
        }
        if(!cinemaRepo.findAllByAddressAndIdNot(address, updateCinemaRequest.getCinemaId()).isEmpty()){
            throw new DataIntegrityViolationException(MessageKeys.ADDRESS_ALREADY_EXIST);
        }
        if(!cinemaRepo.findAllByNameOfCinemaAndIdNot(nameOfCinema, updateCinemaRequest.getCinemaId() ).isEmpty()){
            throw new DataIntegrityViolationException(MessageKeys.NAME_CINEMA_ALREADY_EXIST);
        }
        if(!cinemaRepo.findAllByCodeAndIdNot(code, updateCinemaRequest.getCinemaId()).isEmpty()){
            throw new DataIntegrityViolationException(MessageKeys.CODE_ALREADY_EXIST);
        }
        cinema.setNameOfCinema(updateCinemaRequest.getNameOfCinema());
        cinema.setCode(updateCinemaRequest.getCode());
        cinema.setAddress(updateCinemaRequest.getAddress());
        cinema.setDescription(updateCinemaRequest.getDescription());

        cinemaRepo.save(cinema);

        return cinema;
    }


//    @Transactional
    @Override
    public String deleteCinema(int cinemaId) throws Exception{
        Cinema cinema = cinemaRepo.findById(cinemaId).orElse(null);

        if(cinema == null){
            throw new DataNotFoundException(MessageKeys.CINEMA_DOES_NOT_EXIST);
        }
        List<Room> roomsByCinema = roomRepo.findAllByCinema(cinema);
        if(roomsByCinema.isEmpty()){
            cinemaRepo.delete(cinema);
        }
//        for (Room room: roomsByCinema){
//            if(scheduleRepo.findAllByRoom(room).isEmpty() && seatRepo.findAllByRoom(room).isEmpty()){
//                scheduleRepo.deleteAllByRoom(room);
//                seatRepo.deleteAllByRoom(room);
//            }
//        }
//        roomRepo.deleteAllByCinema(cinema);
        for (Room room: roomRepo.findAll()){
            if(room.getCinema().getId() == cinemaId){
                room.setCinema(null);
                roomRepo.delete(room);
            }
        }

        cinemaRepo.delete(cinema);
        return "Xóa thành cong";
    }


}
