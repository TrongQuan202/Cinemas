package org.example.project_cinemas_java.service.implement;

import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.model.Food;
import org.example.project_cinemas_java.payload.request.admin_request.cinema_request.CreateFoodRequest;
import org.example.project_cinemas_java.payload.request.admin_request.food_request.UpdateFoodRequest;
import org.example.project_cinemas_java.repository.FoodRepo;
import org.example.project_cinemas_java.service.iservice.IFoodService;
import org.example.project_cinemas_java.utils.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class FoodService implements IFoodService {
    @Autowired
    private FoodRepo foodRepo;


    @Override
    public Food createFood(CreateFoodRequest createFoodRequest) throws Exception {
        if(foodRepo.existsByNameOfFood(createFoodRequest.getNameOfFood())){
            throw  new DataIntegrityViolationException(MessageKeys.FOOD_ALREADY_EXIST);
        }

        Food food = Food.builder()
                .nameOfFood(createFoodRequest.getNameOfFood())
                .image(createFoodRequest.getImage())
                .price(createFoodRequest.getPrice())
                .isActive(true)
                .description(createFoodRequest.getDescription())
                .build();
        foodRepo.save(food);

        return food;
    }

    @Override
    public Food updateFood(UpdateFoodRequest updateFoodRequest) throws Exception {
        Food food = foodRepo.findById(updateFoodRequest.getFoodId()).orElse(null);
        if(food == null){
            throw new DataNotFoundException(MessageKeys.FOOD_DOES_NOT_EXIST);
        }
        if (foodRepo.existsByNameOfFoodAndIdNot(updateFoodRequest.getNameOfFood(), updateFoodRequest.getFoodId())){
            throw  new DataIntegrityViolationException(MessageKeys.FOOD_ALREADY_EXIST);
        }
        food.setNameOfFood(updateFoodRequest.getNameOfFood());
        food.setPrice(updateFoodRequest.getPrice());
        food.setDescription(updateFoodRequest.getDescription());
        food.setImage(updateFoodRequest.getImage());
        foodRepo.save(food);

        return food;
    }
}
