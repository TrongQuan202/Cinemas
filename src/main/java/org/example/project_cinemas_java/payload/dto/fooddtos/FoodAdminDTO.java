package org.example.project_cinemas_java.payload.dto.fooddtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FoodAdminDTO {
    private Integer id;
    private String image;
    private String foodName;
    private String description;
    private Double price;
    private String isActive;
}

