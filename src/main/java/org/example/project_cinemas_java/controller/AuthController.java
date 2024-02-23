package org.example.project_cinemas_java.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.project_cinemas_java.exceptions.ConfirmEmailExpired;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.exceptions.DisabledException;
import org.example.project_cinemas_java.model.ConfirmEmail;
import org.example.project_cinemas_java.model.User;
import org.example.project_cinemas_java.payload.request.auth_request.ConfirmCodeRequest;
import org.example.project_cinemas_java.payload.request.auth_request.LoginRequest;
import org.example.project_cinemas_java.payload.request.auth_request.RegisterRequest;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/users")
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
            User user = authService.register(registerRequest);
            confirmEmailService.sendConfirmEmail(user);
            return ResponseEntity.ok().body("Kiểm tra email để xác nhận tài khoản");
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

    @GetMapping("/confirm-register")
    public ResponseEntity<?> confirmEmail (@RequestBody String confirmCode){
        try {
            ConfirmEmail confirmEmail = confirmEmailRepo.findConfirmEmailByConfirmCode(confirmCode);
            User user = userRepo.findByConfirmEmails(confirmEmail);

            var isConfirm = confirmEmailService.confirmEmail(confirmCode);
            if(isConfirm){
                user.setActive(true);
                user.setUserStatus(userStatusRepo.findById(1).orElse(null));
                userRepo.save(user);
            }
            return ResponseEntity.ok().body("Đăng kí thành công");
        } catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }catch (ConfirmEmailExpired ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
