package org.example.project_cinemas_java.service.implement;

import org.example.project_cinemas_java.exceptions.DataIntegrityViolationException;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.exceptions.VoucherExpired;
import org.example.project_cinemas_java.model.Bill;
import org.example.project_cinemas_java.model.Promotion;
import org.example.project_cinemas_java.model.RankCustomer;
import org.example.project_cinemas_java.model.User;
import org.example.project_cinemas_java.payload.converter.PromotionConverter;
import org.example.project_cinemas_java.payload.dto.promotiondtos.PromotionByAdminDTO;
import org.example.project_cinemas_java.payload.dto.promotiondtos.PromotionDTO;
import org.example.project_cinemas_java.payload.dto.promotiondtos.PromotionOfBillDTO;
import org.example.project_cinemas_java.payload.request.promotion_request.PromotionOfBillRequest;
import org.example.project_cinemas_java.repository.BillRepo;
import org.example.project_cinemas_java.repository.PromotionRepo;
import org.example.project_cinemas_java.repository.RankCustomerRepo;
import org.example.project_cinemas_java.repository.UserRepo;
import org.example.project_cinemas_java.service.iservice.IPromotionService;
import org.example.project_cinemas_java.utils.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    @Autowired
    private BillRepo billRepo;
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
    public PromotionOfBillDTO getDiscountAmount(String email, PromotionOfBillRequest promotionOfBillRequest) throws Exception {
        Promotion promotion = promotionRepo.findByName(promotionOfBillRequest.getCode());
        if(promotion == null){
            throw  new DataNotFoundException("Mã voucher không tồn tại");
        }
        User user  = userRepo.findByEmail(email).orElse(null);
        if(user == null){
            throw new DataNotFoundException(MessageKeys.USER_DOES_NOT_EXIST);
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(promotion.getEndTime())) {
            throw new VoucherExpired("Mã voucher đã hết hạn");
        }
        Bill bill = billRepo.findBillByUserAndBillstatusId(user,3);
        if(bill == null){
            throw new DataNotFoundException("Vui lòng thử lại sau ít phút");
        }
        if(bill.getTotalMoney() != promotionOfBillRequest.getTotalMoney()){
            throw new DataIntegrityViolationException("Có lỗi! vui lòng truy cập lại");
        }
        double amountDiscounted = (bill.getTotalMoney() * promotion.getPercent())/100;
        bill.setTotalMoney(bill.getTotalMoney() - amountDiscounted);
        bill.setPromotion(promotion);
        billRepo.save(bill);
        if(promotion.getQuantity() > 0){
            promotion.setQuantity(promotion.getQuantity() - 1);
            promotionRepo.save(promotion);
        }
        PromotionOfBillDTO promotionOfBillDTO = new PromotionOfBillDTO();
        promotionOfBillDTO.setDiscountAmount(amountDiscounted);
        promotionOfBillDTO.setFinalAmount(bill.getTotalMoney());
        promotionOfBillDTO.setTotalMoney(bill.getTotalMoney());
        return promotionOfBillDTO;
    }

    @Override
    public List<PromotionByAdminDTO> getAllPromotionByAdmin() throws Exception {
        List<PromotionByAdminDTO> promotionByAdminDTOS = new ArrayList<>();

        for (Promotion promotion: promotionRepo.findAll()){
            promotionByAdminDTOS.add(promotionToPromotionByAdminDTO(promotion));
        }
        return promotionByAdminDTOS;
    }


    public PromotionByAdminDTO promotionToPromotionByAdminDTO(Promotion promotion){
        PromotionByAdminDTO promotionByAdminDTO = new PromotionByAdminDTO();

        promotionByAdminDTO.setImage(promotion.getImage());
        promotionByAdminDTO.setName(promotion.getName());
        promotionByAdminDTO.setPercent(promotion.getPercent());
        promotionByAdminDTO.setCode(promotion.getCode());

        promotionByAdminDTO.setQuantity(promotion.getQuantity());
        promotionByAdminDTO.setStartTime(localDateTimeToString(promotion.getStartTime()));
        promotionByAdminDTO.setEndTime(localDateTimeToString(promotion.getEndTime()));
        promotionByAdminDTO.setActive(promotion.isActive());
        return promotionByAdminDTO;
    }

    public String localDateTimeToString (LocalDateTime localDateTime){
        // Định dạng LocalDateTime thành chuỗi
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDateTime = localDateTime.format(formatter);
        return formattedDateTime;
    }

}
