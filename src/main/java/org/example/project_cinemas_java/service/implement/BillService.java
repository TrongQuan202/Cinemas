package org.example.project_cinemas_java.service.implement;

import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.model.Bill;
import org.example.project_cinemas_java.model.Promotion;
import org.example.project_cinemas_java.model.User;
import org.example.project_cinemas_java.payload.dto.billdtos.BillDTO;
import org.example.project_cinemas_java.payload.request.bill_request.CreateBillRequest;
import org.example.project_cinemas_java.repository.BillRepo;
import org.example.project_cinemas_java.repository.BillStatusRepo;
import org.example.project_cinemas_java.repository.PromotionRepo;
import org.example.project_cinemas_java.repository.UserRepo;
import org.example.project_cinemas_java.service.iservice.IBillService;
import org.example.project_cinemas_java.utils.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

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
        if(billRepo.existsByUser(user)){
            throw new DataIntegrityViolationException("Bill already exits");
        }
        Bill bill = new Bill();
        bill.setTotalMoney(0);
        bill.setTradingCode(generateCode());
        LocalDateTime timeNow = LocalDateTime.now();
        bill.setCreateTime(timeNow);
        bill.setUser(user);
        bill.setName("Bill of"+ user.getName());
        bill.setUpdateTime(timeNow);
        bill.setPromotion(null);
        bill.setBillstatus(billStatusRepo.findById(3).orElse(null));
        bill.setActive(true);
        billRepo.save(bill);
    }

}
