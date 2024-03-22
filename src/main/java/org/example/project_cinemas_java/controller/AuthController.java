package org.example.project_cinemas_java.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.project_cinemas_java.exceptions.ConfirmEmailExpired;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.exceptions.DisabledException;
import org.example.project_cinemas_java.model.ConfirmEmail;
import org.example.project_cinemas_java.model.User;
import org.example.project_cinemas_java.payload.request.auth_request.*;
import org.example.project_cinemas_java.repository.ConfirmEmailRepo;
import org.example.project_cinemas_java.repository.UserRepo;
import org.example.project_cinemas_java.repository.UserStatusRepo;
import org.example.project_cinemas_java.service.implement.AuthService;
import org.example.project_cinemas_java.service.implement.ConfirmEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest)  {
        try {
              var string = authService.register(registerRequest);
//            userRepo.save(user);
//            confirmEmailService.sendConfirmEmail(user);
            return ResponseEntity.ok().body(string);
        } catch (DataIntegrityViolationException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }catch (IllegalStateException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
        catch (Exception e) {
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
        }catch (DisabledException e){
            // taif khoản bị vô hiệu hóa
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            //lỗi khác do serve
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/confirm-register")
    public ResponseEntity<?> confirmRegister (@RequestParam String confirmCode, @RequestBody RegisterRequest registerRequest){
        try {
            ConfirmEmail confirmEmail = confirmEmailRepo.findConfirmEmailByConfirmCode(confirmCode);
//            User user = userRepo.findByConfirmEmails(confirmEmail);
            boolean isConfirm = confirmEmailService.confirmEmail(confirmCode);
            if(isConfirm){
                authService.saveUser(registerRequest);
                confirmEmail.setUser(null);
                confirmEmailRepo.delete(confirmEmail);
            }
            return ResponseEntity.ok().body("Đăng kí thành công");
        }catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }catch (ConfirmEmailExpired ex){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(ex.getMessage());
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest){
        try {
            var result = authService.changePassword(changePasswordRequest);
            return ResponseEntity.ok().body(result);
        }catch (DataNotFoundException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest){
        try {
            var result = authService.forgotPassword(forgotPasswordRequest);
            return ResponseEntity.ok().body(result);
        }catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @PutMapping("/confirm-new-password")
    public ResponseEntity<?> confirmNewPassword(@RequestBody ConfirmNewPasswordRequest confirmNewPasswordRequest){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        try {
            ConfirmEmail confirmEmail = confirmEmailRepo.findConfirmEmailByConfirmCode(confirmNewPasswordRequest.getConfirmCode());
            User user = userRepo.findByConfirmEmails(confirmEmail);
            var isConfirm = confirmEmailService.confirmEmail(confirmNewPasswordRequest.getConfirmCode());
            if(isConfirm){
                user.setPassword(passwordEncoder.encode(confirmNewPasswordRequest.getNewPassword()));
                userRepo.save(user);
                confirmEmail.setUser(null);
                confirmEmailRepo.delete(confirmEmail);
            }
            return ResponseEntity.ok().body("Tạo mật khẩu mới thành công");
        } catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }catch (ConfirmEmailExpired ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }
}
