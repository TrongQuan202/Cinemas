package org.example.project_cinemas_java.service.iservice;

import org.example.project_cinemas_java.payload.dto.promotiondtos.PromotionDTO;

import java.util.List;

public interface IPromotionService {
    List<PromotionDTO> getAllPromotionByUser(String email)throws Exception;

    double getDiscountAmount(String code, double totalMoney)throws Exception;
}
