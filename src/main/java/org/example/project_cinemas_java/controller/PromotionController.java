package org.example.project_cinemas_java.controller;

import lombok.RequiredArgsConstructor;
import org.example.project_cinemas_java.exceptions.DataIntegrityViolationException;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.exceptions.VoucherExpired;
import org.example.project_cinemas_java.payload.dto.promotiondtos.PromotionByAdminDTO;
import org.example.project_cinemas_java.payload.dto.promotiondtos.PromotionDTO;
import org.example.project_cinemas_java.payload.dto.promotiondtos.PromotionOfBillDTO;
import org.example.project_cinemas_java.payload.dto.seatdtos.SeatStatusDTO;
import org.example.project_cinemas_java.payload.request.promotion_request.CreatePromotionRequest;
import org.example.project_cinemas_java.payload.request.promotion_request.PromotionOfBillRequest;
import org.example.project_cinemas_java.service.implement.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/promotion")
@RequiredArgsConstructor
public class PromotionController {
    @Autowired
    private PromotionService promotionService;

    @GetMapping("/get-all-promotion-by-user")
    public ResponseEntity<?> getAllPromotionByUser(){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                // Lấy email của người dùng từ UserDetails
                String email = userDetails.getUsername();

                List<PromotionDTO> promotionDTOS = promotionService.getAllPromotionByUser(email);
                return ResponseEntity.ok().body(promotionDTOS);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }
        }catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/get-all-promotion-by-admin")
    public ResponseEntity<?> getAllPromotionByAdmin(){
        try {
            List<PromotionByAdminDTO> promotionDTOS = promotionService.getAllPromotionByAdmin();
                return ResponseEntity.ok().body(promotionDTOS);
        }catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/get-promotions")
    public ResponseEntity<?> getAllPromotion(){
          return ResponseEntity.ok().body(promotionService.getAllPromotion());
    }

    @GetMapping("/get-promotion-detail")
    public ResponseEntity<?> getAllPromotion(@RequestParam int id){
        try {
            return ResponseEntity.ok().body(promotionService.getPromotion(id));
        } catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @PostMapping("/create-promotion")
    public ResponseEntity<?> createPromotion(@RequestBody CreatePromotionRequest createPromotionRequest){
        try {
            promotionService.createPromotion(createPromotionRequest);
            return ResponseEntity.ok().body("Thêm thành công");
        }catch (DataIntegrityViolationException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/get-discount-amount")
    public ResponseEntity<?> getDiscountAmount(@RequestBody PromotionOfBillRequest promotionOfBillRequest){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                // Lấy email của người dùng từ UserDetails
                String email = userDetails.getUsername();

                PromotionOfBillDTO promotionDTOS = promotionService.getDiscountAmount(email,promotionOfBillRequest);
                return ResponseEntity.ok().body(promotionDTOS);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }
        }catch (VoucherExpired ex){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(ex.getMessage());
        }catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }catch (DataIntegrityViolationException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }


    }
}
