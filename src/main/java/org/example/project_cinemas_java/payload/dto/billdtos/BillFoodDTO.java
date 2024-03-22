package org.example.project_cinemas_java.payload.dto.billdtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BillFoodDTO {
    private Integer quantityFood;
    private String foodName;
}
