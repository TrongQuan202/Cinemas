package org.example.project_cinemas_java.payload.converter;

import org.example.project_cinemas_java.model.Food;
import org.example.project_cinemas_java.payload.dto.fooddtos.FoodDTO;
import org.example.project_cinemas_java.payload.dto.fooddtos.ListFoodDTO;
import org.springframework.stereotype.Component;

@Component
public class FoodConverter {
    public FoodDTO foodToFoodDTO (Food food){
        FoodDTO foodDTO = FoodDTO.builder()
                .id(food.getId())
                .image(food.getImage())
                .foodName(food.getNameOfFood())
                .description(food.getDescription())
                .price(food.getPrice())
                .build();
        return foodDTO;
    }

}
