package org.example.project_cinemas_java.controller;

import lombok.RequiredArgsConstructor;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.model.Food;
import org.example.project_cinemas_java.model.Seat;
import org.example.project_cinemas_java.payload.dto.fooddtos.FoodAdminDTO;
import org.example.project_cinemas_java.payload.dto.fooddtos.FoodDTO;
import org.example.project_cinemas_java.payload.request.admin_request.cinema_request.CreateFoodRequest;
import org.example.project_cinemas_java.payload.request.admin_request.food_request.UpdateFoodRequest;
import org.example.project_cinemas_java.payload.request.admin_request.seat_request.CreateSeatRequest;
import org.example.project_cinemas_java.payload.request.admin_request.seat_request.UpdateSeatRequest;
import org.example.project_cinemas_java.service.implement.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/food")
@RequiredArgsConstructor
public class FoodController {
    @Autowired
    private FoodService foodService;

    @PostMapping("/create-food")
    public ResponseEntity<?> createFood(@RequestBody CreateFoodRequest createFoodRequest){
        try {
            Food food = foodService.createFood(createFoodRequest);
            return ResponseEntity.ok().body(food);
        }catch (DataIntegrityViolationException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/get-food-by-admin")
    public ResponseEntity<?> getFoodByAdmin(@RequestParam int foodId){
        try {
            FoodAdminDTO foodAdminDTO = foodService.getFoodByAdmin(foodId);
            return ResponseEntity.ok().body(foodAdminDTO);
        }catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/update-food")
    public ResponseEntity<?> editFood(@RequestBody FoodAdminDTO foodAdminDTO){
        try {
            FoodAdminDTO foodAdminDTO1 = foodService.editFoodByAdmin(foodAdminDTO);
            return ResponseEntity.ok().body(foodAdminDTO1);
        }catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

//    @PutMapping("/update-food")
//    public ResponseEntity<?> updateFood(@RequestBody UpdateFoodRequest updateFoodRequest){
//        try {
//            Food food = foodService.updateFood(updateFoodRequest);
//            return ResponseEntity.ok().body(food);
//        }catch (DataIntegrityViolationException ex){
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
//        }catch (DataNotFoundException ex){
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
//        }catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
//        }
//    }

   @GetMapping("/get-all-food")
    public ResponseEntity<?> getAllFood() {
        try {
            List<FoodDTO> result = foodService.getAllFood();
            if (result.size() == 0) {
                return ResponseEntity.ok().body(null);
            } else {
                return ResponseEntity.ok(result);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/get-all-food-admin")
    public ResponseEntity<?> getAllFoodAdmin() {
        try {
            List<FoodAdminDTO> result = foodService.getAllFoodAdmin();
            if (result.size() == 0) {
                return ResponseEntity.ok().body(null);
            } else {
                return ResponseEntity.ok(result);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
