package org.example.project_cinemas_java.service.iservice;

import org.example.project_cinemas_java.model.Food;
import org.example.project_cinemas_java.payload.request.admin_request.cinema_request.CreateFoodRequest;
import org.example.project_cinemas_java.payload.request.admin_request.food_request.UpdateFoodRequest;

public interface IFoodService {
    Food createFood(CreateFoodRequest createFoodRequest) throws Exception;

    Food updateFood(UpdateFoodRequest updateFoodRequest) throws Exception;
}
