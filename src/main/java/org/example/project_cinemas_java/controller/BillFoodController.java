package org.example.project_cinemas_java.controller;

import lombok.RequiredArgsConstructor;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.service.implement.BillFoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/billFood")
@RequiredArgsConstructor
public class BillFoodController {
    @Autowired
    private BillFoodService billFoodService;

    @PostMapping("/create-billFood")
    public ResponseEntity<?> createBillFood(@RequestParam String email, int foodId){
        try {
            billFoodService.createBillFood(foodId,email);
            return ResponseEntity.ok().body("create success");
        }catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/remove-billFood")
    public ResponseEntity<?> removeBillFood(@RequestParam String email,int foodId){
        try {
            billFoodService.removeBillFood(foodId,email);
            return ResponseEntity.ok().body("create success");
        }catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
