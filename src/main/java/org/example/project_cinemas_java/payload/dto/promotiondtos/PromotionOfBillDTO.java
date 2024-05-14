package org.example.project_cinemas_java.payload.dto.promotiondtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PromotionOfBillDTO {
    private double finalAmount;
    private double discountAmount;

    private double totalMoney;
}
