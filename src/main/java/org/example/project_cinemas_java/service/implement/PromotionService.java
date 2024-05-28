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
import org.example.project_cinemas_java.payload.request.promotion_request.CreatePromotionRequest;
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
import java.util.Random;

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
        List<PromotionDTO> promotionDTOS = new ArrayList<>();
        if(!promotions.isEmpty()){
            for (Promotion promotion: promotions){
                if(promotion.getQuantity() > 0){
                    promotionDTOS.add(promotionConverter.promotionToPromotionDTO(promotion));
                }

            }
        }
        return promotionDTOS;
    }

    @Override
    public PromotionOfBillDTO getDiscountAmount(String email, PromotionOfBillRequest promotionOfBillRequest) throws Exception {
        Promotion promotion = promotionRepo.findByCode(promotionOfBillRequest.getCode());
        if(promotion == null){
            throw  new DataNotFoundException("Mã voucher không tồn tại");
        }
        if(promotion.getQuantity() <1){
            throw new DataIntegrityViolationException("Voucher đã hết hạn");
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

    @Override
    public void createPromotion(CreatePromotionRequest createPromotionRequest) throws Exception {
        Promotion promotion = promotionRepo.findByName(createPromotionRequest.getName());
        if(promotion != null){
            throw new DataIntegrityViolationException("Mã khuyến mãi đã tồn tại");
        }
        Promotion promotion1 = new Promotion();
        promotion1.setQuantity(createPromotionRequest.getQuantity());
        promotion1.setCode(generateConfirmCode());
        promotion1.setImage(createPromotionRequest.getImage());
        promotion1.setName(createPromotionRequest.getName());
        promotion1.setStartTime(stringToLocalDateTime(createPromotionRequest.getStart()));
        promotion1.setEndTime(stringToLocalDateTime(createPromotionRequest.getEnd()));
        promotion1.setPercent(createPromotionRequest.getPercent());
        promotion1.setActive(true);
        promotion1.setRankcustomer(rankCustomerRepo.findById(1).orElse(null));
        promotion1.setDescription(createPromotionRequest.getContent());
        promotionRepo.save(promotion1);
    }

    @Override
    public List<Promotion> getAllPromotion() {
        return promotionRepo.findAll();
    }

    @Override
    public Promotion getPromotion(int id) throws Exception {
        Promotion promotion = promotionRepo.findById(id).orElse(null);
        if(promotion == null){
            throw new DataNotFoundException("Sự kiện đã kết thúc");
        }
        return promotion;
    }

    public String generateConfirmCode() {
        Random random = new Random();
        int randomNumber = random.nextInt(900000) + 100000;
        return String.valueOf(randomNumber);
    }

    public LocalDateTime stringToLocalDateTime (String time){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        // Phân tích chuỗi ngày giờ với formatter
        LocalDateTime localDateTime = LocalDateTime.parse(time, formatter);
        return localDateTime;
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
