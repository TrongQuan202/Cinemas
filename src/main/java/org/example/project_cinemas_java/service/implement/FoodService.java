package org.example.project_cinemas_java.service.implement;

import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.model.Food;
import org.example.project_cinemas_java.model.User;
import org.example.project_cinemas_java.payload.converter.FoodConverter;
import org.example.project_cinemas_java.payload.dto.fooddtos.FoodAdminDTO;
import org.example.project_cinemas_java.payload.dto.fooddtos.FoodDTO;
import org.example.project_cinemas_java.payload.dto.fooddtos.ListFoodDTO;
import org.example.project_cinemas_java.payload.request.admin_request.cinema_request.CreateFoodRequest;
import org.example.project_cinemas_java.payload.request.admin_request.food_request.UpdateFoodRequest;
import org.example.project_cinemas_java.payload.request.auth_request.RegisterRequest;
import org.example.project_cinemas_java.repository.FoodRepo;
import org.example.project_cinemas_java.service.iservice.IFoodService;
import org.example.project_cinemas_java.utils.MessageKeys;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FoodService implements IFoodService {
    @Autowired
    private FoodRepo foodRepo;
    @Autowired
    private FoodConverter foodConverter;

    @Autowired
    private ModelMapper modelMapper;

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

//    @Override
//    public Food updateFood(UpdateFoodRequest updateFoodRequest) throws Exception {
//        Food food = foodRepo.findById(updateFoodRequest.getFoodId()).orElse(null);
//        if(food == null){
//            throw new DataNotFoundException(MessageKeys.FOOD_DOES_NOT_EXIST);
//        }
//        if (foodRepo.existsByNameOfFoodAndIdNot(updateFoodRequest.getNameOfFood(), updateFoodRequest.getFoodId())){
//            throw  new DataIntegrityViolationException(MessageKeys.FOOD_ALREADY_EXIST);
//        }
//        food.setNameOfFood(updateFoodRequest.getNameOfFood());
//        food.setPrice(updateFoodRequest.getPrice());
//        food.setDescription(updateFoodRequest.getDescription());
//        food.setImage(updateFoodRequest.getImage());
//        foodRepo.save(food);
//
//        return food;
//    }

    @Override
    public List<FoodDTO> getAllFood() {
        List<FoodDTO> foodDTO = new ArrayList<>();
        for (Food food:foodRepo.findAll()){
            foodDTO.add(foodConverter.foodToFoodDTO(food));
        }
        return foodDTO;
    }

    @Override
    public FoodAdminDTO getFoodByAdmin(int foodId) throws Exception {
        Food food = foodRepo.findById(foodId).orElse(null);
        if(food == null){
            throw new DataNotFoundException("Combo này không tồn tại");
        }

        return foodToFoodAdminDTO(food);
    }

    @Override
    public FoodAdminDTO editFoodByAdmin(FoodAdminDTO foodAdminDTO) throws Exception {
        Food food= foodRepo.findByNameOfFood(foodAdminDTO.getFoodName());
        if(food ==null){
            throw new DataNotFoundException("Combo này không tồn tại");
        }
        food.setImage(foodAdminDTO.getImage());
        food.setNameOfFood(foodAdminDTO.getFoodName());
        food.setPrice(foodAdminDTO.getPrice());
        food.setDescription(foodAdminDTO.getDescription());
        food.setActive("Còn hàng".equals(foodAdminDTO.getIsActive()) ? true :false);
        foodRepo.save(food);

        FoodAdminDTO foodAdminDTO1 = foodToFoodAdminDTO(food);
        return foodAdminDTO1;
    }

    public List<FoodAdminDTO> getAllFoodAdmin() {
        List<FoodAdminDTO> foodDTO = new ArrayList<>();
        for (Food food:foodRepo.findAll()){
            foodDTO.add(foodToFoodAdminDTO(food));
        }
        return foodDTO;
    }



    public FoodAdminDTO foodToFoodAdminDTO(Food food){
        FoodAdminDTO foodAdminDTO = new FoodAdminDTO();
        foodAdminDTO.setFoodName(food.getNameOfFood());
        foodAdminDTO.setId(food.getId());
        foodAdminDTO.setPrice(food.getPrice());
        foodAdminDTO.setImage(food.getImage());
        foodAdminDTO.setIsActive(food.isActive() ? "Còn hàng" : "Hết hàng");
        foodAdminDTO.setDescription(food.getDescription());
        return foodAdminDTO;
    }
}
