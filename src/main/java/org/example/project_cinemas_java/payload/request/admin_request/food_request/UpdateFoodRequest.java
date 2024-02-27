package org.example.project_cinemas_java.payload.request.admin_request.food_request;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateFoodRequest {
    private int foodId;
    private double price;

    private String description;

    private String image;

    private String nameOfFood;
}
