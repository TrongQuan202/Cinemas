package org.example.project_cinemas_java.payload.dto.fooddtos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FoodDTO {
    private Integer id;
    private String image;
    private String foodName;
    private String description;
    private Double price;
}
