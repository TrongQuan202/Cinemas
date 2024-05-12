package org.example.project_cinemas_java.payload.request.promotion_request;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PromotionOfBillRequest {
    private String code;
    private double totalMoney;
}
