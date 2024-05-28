package org.example.project_cinemas_java.service.iservice;

import org.example.project_cinemas_java.model.Promotion;
import org.example.project_cinemas_java.payload.dto.promotiondtos.PromotionByAdminDTO;
import org.example.project_cinemas_java.payload.dto.promotiondtos.PromotionDTO;
import org.example.project_cinemas_java.payload.dto.promotiondtos.PromotionOfBillDTO;
import org.example.project_cinemas_java.payload.request.promotion_request.CreatePromotionRequest;
import org.example.project_cinemas_java.payload.request.promotion_request.PromotionOfBillRequest;

import java.util.List;

public interface IPromotionService {
    List<PromotionDTO> getAllPromotionByUser(String email)throws Exception;

    PromotionOfBillDTO getDiscountAmount(String email, PromotionOfBillRequest promotionOfBillRequest)throws Exception;

    List<PromotionByAdminDTO> getAllPromotionByAdmin()throws Exception;

    void createPromotion(CreatePromotionRequest createPromotionRequest) throws Exception;

    List<Promotion> getAllPromotion() ;

    Promotion getPromotion(int id)throws Exception;
}
