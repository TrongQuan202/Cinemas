package org.example.project_cinemas_java.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.project_cinemas_java.exceptions.ConfirmEmailExpired;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.exceptions.DisabledException;
import org.example.project_cinemas_java.exceptions.TokenRefreshException;
import org.example.project_cinemas_java.model.ConfirmEmail;
import org.example.project_cinemas_java.model.User;
import org.example.project_cinemas_java.payload.dto.userdto.UserDTO;
import org.example.project_cinemas_java.payload.request.auth_request.*;
import org.example.project_cinemas_java.repository.ConfirmEmailRepo;
import org.example.project_cinemas_java.repository.UserRepo;
import org.example.project_cinemas_java.repository.UserStatusRepo;
import org.example.project_cinemas_java.service.implement.AuthService;
import org.example.project_cinemas_java.service.implement.ConfirmEmailService;
import org.example.project_cinemas_java.service.implement.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private ConfirmEmailService confirmEmailService;

    @Autowired
    private ConfirmEmailRepo confirmEmailRepo;

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private UserStatusRepo userStatusRepo;
    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping("/confirmEmail")
    public ResponseEntity<?> confirmEmail(@Valid @RequestParam String email) {
        try {
            var string = authService.confirmEmail(email);
//            userRepo.save(user);
//            confirmEmailService.sendConfirmEmail(user);
            return ResponseEntity.ok().body(string);
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            var loginDTO = authService.login(loginRequest);
            return ResponseEntity.ok().body(loginDTO);
        } catch (DataNotFoundException e) {
            // Email không tồn tại
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AuthenticationException e) {
            // Sai mật khẩu hoặc thông tin đăng nhập không hợp lệ
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (DisabledException e) {
            // taif khoản bị vô hiệu hóa
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            //lỗi khác do serve
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> confirmRegister( @RequestBody RegisterRequest registerRequest) {
        try {
            ConfirmEmail confirmEmail = confirmEmailRepo.findConfirmEmailByConfirmCodeAndEmailUser(registerRequest.getConfirmCode(), registerRequest.getEmail());
//            User user = userRepo.findByConfirmEmails(confirmEmail);
            boolean isConfirm = confirmEmailService.checkCodeForEmail(registerRequest.getConfirmCode(), registerRequest.getEmail());
            if (isConfirm) {
                authService.register(registerRequest);
            }
            return ResponseEntity.ok().body("Đăng kí thành công");
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (ConfirmEmailExpired ex) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(ex.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                // Lấy email của người dùng từ UserDetails
                String email = userDetails.getUsername();

                String s = authService.changePassword(email,changePasswordRequest);
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

    @PutMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        try {
            var result = authService.forgotPassword(forgotPasswordRequest);
            return ResponseEntity.ok().body(result);
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/confirm-new-password")
    public ResponseEntity<?> confirmNewPassword(@RequestBody ConfirmNewPasswordRequest confirmNewPasswordRequest) {
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        try {
//            ConfirmEmail confirmEmail = confirmEmailRepo.findConfirmEmailByConfirmCode(confirmNewPasswordRequest.getConfirmCode());
//            User user = userRepo.findByConfirmEmails(confirmEmail);
////            var isConfirm = confirmEmailService.checkCodeForEmail(confirmNewPasswordRequest.getConfirmCode());
//            if(isConfirm){
//                user.setPassword(passwordEncoder.encode(confirmNewPasswordRequest.getNewPassword()));
//                userRepo.save(user);
//                confirmEmail.setUser(null);
//                confirmEmailRepo.delete(confirmEmail);
//            }
        return ResponseEntity.ok().body("Tạo mật khẩu mới thành công");
//        } catch (DataNotFoundException ex){
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
//        }catch (ConfirmEmailExpired ex) {
//            return ResponseEntity.badRequest().body(ex.getMessage());
//        }catch (Exception e){
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
//        }
//
//    }
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestParam String refreshToken) {
        try {
            var result = refreshTokenService.refreshToken(refreshToken);
            return ResponseEntity.ok().body(result);
        } catch (TokenRefreshException ex){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        }catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
