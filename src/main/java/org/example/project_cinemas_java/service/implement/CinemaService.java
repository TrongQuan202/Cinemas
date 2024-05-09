package org.example.project_cinemas_java.service.implement;

import jakarta.persistence.Tuple;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.model.*;
import org.example.project_cinemas_java.payload.dto.cinemadtos.CinemaNameDTO;
import org.example.project_cinemas_java.payload.dto.cinemadtos.RevenueCinemaDTO;
import org.example.project_cinemas_java.payload.request.admin_request.cinema_request.CreateCinemaRequest;
import org.example.project_cinemas_java.payload.request.admin_request.cinema_request.UpdateCinemaRequest;
import org.example.project_cinemas_java.repository.*;
import org.example.project_cinemas_java.service.iservice.ICinemaService;
import org.example.project_cinemas_java.utils.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
    @Autowired
    private TicketRepo ticketRepo;
    @Autowired
    private BillTicketRepo billTicketRepo;
    @Autowired
    private BillRepo billRepo;

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

    @Override
    public List<String> getCinemaByAddress(String address) throws Exception {
        if(cinemaRepo.findByAddressContaining(address).isEmpty()){
            throw new DataNotFoundException(MessageKeys.CINEMA_DOES_NOT_EXIST);
        }
        return cinemaRepo.findByAddressContaining(address);
    }

    @Override
    public List<Object[]> getRevenueListByCinema(int cinemaId, int year) throws Exception {
        Cinema cinema = cinemaRepo.findById(cinemaId).orElse(null);
        if(cinema == null){
            throw new DataNotFoundException(MessageKeys.CINEMA_DOES_NOT_EXIST);
        }

        //todo lấy tất cả room theo cinema
        List<Room> rooms = roomRepo.findAllByCinema(cinema);
        if(rooms.size() < 1){
            throw new DataNotFoundException(MessageKeys.ROOM_DOES_NOT_EXIST);
        }

        //todo lấy tất cả schedue theo list room của cinema
        List<Schedule> schedulesByRoom = new ArrayList<>();
        for (Room room:rooms){
            List<Schedule> schedules = scheduleRepo.findAllByRoom(room);
            schedulesByRoom.addAll(schedules);
        }
        if(schedulesByRoom.size() < 1){
            throw new DataNotFoundException(MessageKeys.SCHEDULE_DOES_NOT_EXIST);
        }

        //todo lấy  ticket theo danh sách schedule
        List<Ticket> ticketsBySchedule = new ArrayList<>();
        for (Schedule schedule:schedulesByRoom){
            List<Ticket> tickets = ticketRepo.findAllByScheduleAndCodeNotNullAndPriceTicketGreaterThan(schedule,0);
            ticketsBySchedule.addAll(tickets);
        }

        //todo lấy billTicket theo danh sách ticket
        List<BillTicket> billTicketsByTicket = new ArrayList<>();
        for (Ticket ticket:ticketsBySchedule){
            billTicketsByTicket.add(billTicketRepo.findByTicket(ticket));
        }

        //toto lấy ra danh sách bill theo danh sách billTicket
        List<Bill> bills = billRepo.findDistinctByBillTicketsIn(billTicketsByTicket);

        List<Integer> billIds = new ArrayList<>();
        for (Bill bill : bills) {
            billIds.add(bill.getId());
        }
        List<Object[]> objects = billRepo.getMonthlyRevenue(year,billIds);

        return objects;
    }

    @Override
    public List<String> getAllCinemaName() {
        List<String> cinemaNameDTOS = new ArrayList<>();
        for (Cinema cinema:cinemaRepo.findAll()){
            cinemaNameDTOS.add(cinema.getNameOfCinema());
        }
        return cinemaNameDTOS;
    }

    @Override
    public List<RevenueCinemaDTO> getRevenueCinema() throws Exception {
        List<Tuple> s = cinemaRepo.findRevenueByMonth();
        List<RevenueCinemaDTO> revenueCinemaDTOS = s.stream()
                .map(t -> new RevenueCinemaDTO(
                        t.get(0, String.class),
                        t.get(1, Double.class)
                )).collect(Collectors.toList());

        return revenueCinemaDTOS;
    }


}
