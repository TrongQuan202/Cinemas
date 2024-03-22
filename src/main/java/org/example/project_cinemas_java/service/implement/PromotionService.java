package org.example.project_cinemas_java.service.implement;

import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.exceptions.VoucherExpired;
import org.example.project_cinemas_java.model.Promotion;
import org.example.project_cinemas_java.model.RankCustomer;
import org.example.project_cinemas_java.model.User;
import org.example.project_cinemas_java.payload.converter.PromotionConverter;
import org.example.project_cinemas_java.payload.dto.promotiondtos.PromotionDTO;
import org.example.project_cinemas_java.repository.PromotionRepo;
import org.example.project_cinemas_java.repository.RankCustomerRepo;
import org.example.project_cinemas_java.repository.UserRepo;
import org.example.project_cinemas_java.service.iservice.IPromotionService;
import org.example.project_cinemas_java.utils.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PromotionService implements IPromotionService {
    @Autowired
    private PromotionRepo promotionRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private RankCustomerRepo rankCustomerRepo;

    private final PromotionConverter promotionConverter;

    public PromotionService(PromotionConverter promotionConverter) {
        this.promotionConverter = promotionConverter;
    }

    @Override
    public List<PromotionDTO> getAllPromotionByUser(String email) throws Exception {
        User user = userRepo.findByEmail(email).orElse(null);
        if(user == null){
            throw new DataNotFoundException(MessageKeys.USER_DOES_NOT_EXIST);
        }
        RankCustomer rankCustomer = rankCustomerRepo.findByUsers(user);
        if(rankCustomer == null){
            throw new DataNotFoundException(MessageKeys.RANK_OF_USER_DOES_NOT_EXIST);
        }
        List<Promotion> promotions = promotionRepo.findAllByRankcustomer(rankCustomer);
        if(promotions.size() ==0 ){
            throw  new DataNotFoundException("Promotion does not exist");
        }
        List<PromotionDTO> promotionDTOS = new ArrayList<>();
        for (Promotion promotion: promotions){
            promotionDTOS.add(promotionConverter.promotionToPromotionDTO(promotion));
        }
        return promotionDTOS;
    }

    @Override
    public double getDiscountAmount(String code, double totalMoney) throws Exception {
        Promotion promotion = promotionRepo.findByName(code);
        if(promotion == null){
            throw  new DataNotFoundException("Promotion does not exist");
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(promotion.getEndTime())) {
            throw new VoucherExpired("Voucher Expired");
        }
        double amountDiscounted = (totalMoney * promotion.getPercent())/100;
        return amountDiscounted;
    }
}
