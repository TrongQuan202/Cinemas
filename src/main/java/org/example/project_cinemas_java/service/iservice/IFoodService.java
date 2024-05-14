package org.example.project_cinemas_java.service.iservice;

import org.example.project_cinemas_java.model.Food;
import org.example.project_cinemas_java.payload.dto.fooddtos.FoodAdminDTO;
import org.example.project_cinemas_java.payload.dto.fooddtos.FoodDTO;
import org.example.project_cinemas_java.payload.request.admin_request.cinema_request.CreateFoodRequest;
import org.example.project_cinemas_java.payload.request.admin_request.food_request.UpdateFoodRequest;

import java.util.List;

public interface IFoodService {
    Food createFood(CreateFoodRequest createFoodRequest) throws Exception;

//    Food updateFood(UpdateFoodRequest updateFoodRequest) throws Exception;

    List<FoodDTO> getAllFood();

    FoodAdminDTO getFoodByAdmin(int foodId) throws Exception;

    FoodAdminDTO editFoodByAdmin(FoodAdminDTO foodAdminDTO)throws Exception;
}
