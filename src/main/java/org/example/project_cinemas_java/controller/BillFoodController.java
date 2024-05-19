package org.example.project_cinemas_java.controller;

import lombok.RequiredArgsConstructor;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.payload.dto.promotiondtos.PromotionOfBillDTO;
import org.example.project_cinemas_java.payload.request.food_request.ChooseFoodRequest;
import org.example.project_cinemas_java.service.implement.BillFoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    @PutMapping("/update-billFood")
    public ResponseEntity<?> update(@RequestBody ChooseFoodRequest chooseFoodRequest ){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                // Lấy email của người dùng từ UserDetails
                String email = userDetails.getUsername();

                PromotionOfBillDTO s = billFoodService.chooseFood(email,chooseFoodRequest);
                return ResponseEntity.ok().body(s);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }
        } catch (DataNotFoundException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
