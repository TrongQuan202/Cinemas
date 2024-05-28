package org.example.project_cinemas_java.payload.converter;

import org.example.project_cinemas_java.model.Promotion;
import org.example.project_cinemas_java.payload.dto.promotiondtos.PromotionDTO;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class PromotionConverter {
    public PromotionDTO promotionToPromotionDTO (Promotion promotion){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy"); // Định dạng ngày giờ
        String formattedEndTime = promotion.getEndTime().format(formatter); // Chuyển đổi endTime sang String
        PromotionDTO promotionDTO = PromotionDTO.builder()
                .name(promotion.getCode())
                .description(promotion.getName())
                .quantity(promotion.getQuantity())
                .endTime(formattedEndTime)
                .build();
        return promotionDTO;
    }
}
