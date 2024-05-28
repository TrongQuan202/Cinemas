package org.example.project_cinemas_java.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.project_cinemas_java.exceptions.DataIntegrityViolationException;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.payload.dto.seatdtos.SeatStatusDTO;
import org.example.project_cinemas_java.payload.dto.userdto.UserAccountDTO;
import org.example.project_cinemas_java.payload.dto.userdto.UserDTO;
import org.example.project_cinemas_java.payload.request.auth_request.ConfirmAuthorRequest;
import org.example.project_cinemas_java.service.implement.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/user")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/get-phone-user")
    public ResponseEntity<?> getPhoneNumberById(@RequestParam int id){
        try {
            String phone  = userService.getPhoneNumberById(id);
            return ResponseEntity.ok().body(phone);
        }catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/get-all-user-by-admin")
    public ResponseEntity<?> getAllUserByAdmin(){
            List<UserDTO> userDTOS = userService.getAllUserByAdmin();
            return ResponseEntity.ok().body(userDTOS);
    }

    @GetMapping("/get-profile-user")
    public ResponseEntity<?> getProfileUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                // Lấy email của người dùng từ UserDetails
                String email = userDetails.getUsername();

                UserAccountDTO s = userService.getProfileUser(email);
                return ResponseEntity.ok().body(s);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping ("/confirm-author")
    public ResponseEntity<?> confirmAuth(@Valid  @RequestBody ConfirmAuthorRequest confirmAuthorRequest) throws Exception {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                // Lấy email của người dùng từ UserDetails
                String email = userDetails.getUsername();

                List<UserDTO> s = userService.confirmAuth(email,confirmAuthorRequest);
                return ResponseEntity.ok().body(s);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }
        }catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }catch (DataIntegrityViolationException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/get-point")
    public ResponseEntity<?> getPoint() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                // Lấy email của người dùng từ UserDetails
                String email = userDetails.getUsername();

                float point = userService.getPoint(email);
                return ResponseEntity.ok().body(point);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
