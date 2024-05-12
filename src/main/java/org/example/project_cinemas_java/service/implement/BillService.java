package org.example.project_cinemas_java.service.implement;

import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.model.*;
import org.example.project_cinemas_java.payload.dto.billdtos.BillDTO;
import org.example.project_cinemas_java.payload.request.bill_request.CreateBillRequest;
import org.example.project_cinemas_java.repository.*;
import org.example.project_cinemas_java.service.iservice.IBillService;
import org.example.project_cinemas_java.utils.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Service
public class BillService implements IBillService {
    @Autowired
    private BillRepo billRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PromotionRepo promotionRepo;
    @Autowired
    private BillStatusRepo billStatusRepo;

    @Autowired
    private BillTicketRepo billTicketRepo;

    @Autowired
    private TicketRepo ticketRepo;


    private String generateCode() {
        Random random = new Random();
        int randomNumber = random.nextInt(900000) + 100000;
        return String.valueOf(randomNumber);
    }
    @Override
    public void createBill(String email) throws Exception {
        User user = userRepo.findByEmail(email).orElse(null);
        if(user == null){
            throw new DataNotFoundException(MessageKeys.USER_DOES_NOT_EXIST);
        }
        Bill billByUser = billRepo.findBillByUserAndBillstatusId(user,3);

        if( billByUser != null){
            billByUser.setTotalMoney(0);
            billByUser.setTradingCode(generateCode());
            LocalDateTime timeNow = LocalDateTime.now();
            billByUser.setCreateTime(timeNow);
            billByUser.setMonth(timeNow.getMonthValue());
            billByUser.setUser(user);
            billByUser.setName("Bill of"+ user.getName());
            billByUser.setUpdateTime(timeNow);
            billByUser.setPromotion(null);
            billByUser.setBillstatus(billStatusRepo.findById(3).orElse(null));
            billByUser.setActive(true);
            billRepo.save(billByUser);

        }else {
            Bill bill = new Bill();
            bill.setTotalMoney(0);
            bill.setTradingCode(generateCode());
            LocalDateTime timeNow = LocalDateTime.now();
            bill.setCreateTime(timeNow);
            bill.setMonth(timeNow.getMonthValue());
            bill.setUser(user);
            bill.setName("Bill of"+ user.getName());
            bill.setUpdateTime(timeNow);
            bill.setPromotion(null);
            bill.setBillstatus(billStatusRepo.findById(3).orElse(null));
            bill.setActive(true);
            billRepo.save(bill);
        }
    }

    @Override
    public String saveBillInformation(int user) throws Exception {
        User exitstingUser = userRepo.findById(user).orElse(null);
        if(exitstingUser == null){
            throw new DataNotFoundException("Thông tin khách hàng bị lỗi! Thử lại sau ít phút");
        }

        //tìm bill chua thanh toan
        Bill bill = billRepo.findBillByUserAndBillstatusId(exitstingUser,3);
        if(bill == null){
            throw new DataNotFoundException("Không tìm thấy đơn hàng");
        }
        Promotion promotion = bill.getPromotion();
        if (promotion.getQuantity() > 0){
            promotion.setQuantity(promotion.getQuantity() - 1);
            promotionRepo.save(promotion);
        }

        bill.setName(exitstingUser.getUserName() + "đã thanh toán hóa đơn");
        bill.setBillstatus(billStatusRepo.findById(2).orElse(null));
        bill.setUpdateTime(LocalDateTime.now());
        bill.setCreateTime(LocalDateTime.now());
        billRepo.save(bill);

        Set<BillTicket> billTickets = billTicketRepo.findAllByBill(bill);
        if(billTickets.isEmpty()){
            throw new DataNotFoundException("Vui lòng chọn combo");
        }
        List<Ticket> tickets = new ArrayList<>();
        for (BillTicket billTicket: billTickets){
            tickets.add(billTicket.getTicket());
        }
        for (Ticket ticket:tickets){
            ticket.setSeatStatus(4);
            ticketRepo.save(ticket);
        }
        return bill.getTradingCode();
    }

}
