package org.example.project_cinemas_java.controller;

import lombok.RequiredArgsConstructor;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.payload.dto.billdtos.BillAdminDTO;
import org.example.project_cinemas_java.payload.dto.billdtos.HistoryBillByUserDTO;
import org.example.project_cinemas_java.payload.request.DeleteByTimeRequest;
import org.example.project_cinemas_java.repository.BillRepo;
import org.example.project_cinemas_java.service.implement.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/bill")
@RequiredArgsConstructor
public class BillController {
    @Autowired
    private BillService billService;

    @Autowired
    private BillRepo billRepo;

    @PostMapping("/create-bill")
    public ResponseEntity<?> createBill(){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                // Lấy email của người dùng từ UserDetails
                String email = userDetails.getUsername();
                // Gọi service để tạo hóa đơn
                billService.createBill(email);
                return ResponseEntity.ok().body("create bill success");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }
        }catch (DataIntegrityViolationException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/delete-bill")
    public ResponseEntity<?> deleteBillByAdmin(@RequestBody DeleteByTimeRequest deleteByTimeRequest) {
        /*       try {*/
        String start = deleteByTimeRequest.getStart();
        String end = deleteByTimeRequest.getEnd();
        billRepo.deleteBill(start,end);
        return ResponseEntity.ok().body("Xóa đơn hàng thành công");

    }

    @GetMapping("/get-all-bill")
    public ResponseEntity<?> getAllBillAdmin(){
        try {
            List<BillAdminDTO> billAdminDTOS = billService.getAllBillAdmin();
            return ResponseEntity.ok().body(billAdminDTOS);
        }catch (DataIntegrityViolationException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping("/get-totalMoney")
    public ResponseEntity<?> getTotalMoney(@RequestParam int user){
        try {
            double totalMoney = billService.getTotalMoney(user);
            return ResponseEntity.ok().body(totalMoney);
        }catch (DataIntegrityViolationException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/get-history-bill-by-user")
    public ResponseEntity<?> getAllHistoryBillByUser(){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                // Lấy email của người dùng từ UserDetails
                String email = userDetails.getUsername();
                // Gọi service để tạo hóa đơn
                List<HistoryBillByUserDTO> historyBillByUserDTOS = billService.getAllHistoryBillByUser(email);
                return ResponseEntity.ok().body(historyBillByUserDTOS);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }
        }catch (DataIntegrityViolationException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
