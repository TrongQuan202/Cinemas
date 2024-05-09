package org.example.project_cinemas_java.service.implement;

import org.apache.commons.lang3.RandomStringUtils;
import org.example.project_cinemas_java.components.JwtTokenUtils;
import org.example.project_cinemas_java.exceptions.ConfirmEmailExpired;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.model.ConfirmEmail;
import org.example.project_cinemas_java.model.RefreshToken;
import org.example.project_cinemas_java.model.Role;
import org.example.project_cinemas_java.model.User;
import org.example.project_cinemas_java.payload.dto.authdtos.LoginDTO;
import org.example.project_cinemas_java.payload.request.auth_request.*;
import org.example.project_cinemas_java.repository.*;
import org.example.project_cinemas_java.service.iservice.IAuthService;
import org.example.project_cinemas_java.utils.MessageKeys;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthService  implements IAuthService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RankCustomerRepo rankCustomerRepo;
    @Autowired
    private UserStatusRepo userStatusRepo;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtTokenUtils jwtTokenUtils;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private ConfirmEmailService confirmEmailService;
    @Autowired
    private ConfirmEmailRepo confirmEmailRepo;

    @Value("${jwt.expiration}")
    private int expirationToken;

    @Value("${jwt.expirationRefreshToken}")
    private int expirationRefreshToke;

    @Override
    public String confirmEmail(String email) throws Exception {

        if(userRepo.existsByEmail(email)){
            throw new DataIntegrityViolationException(MessageKeys.EMAIL_ALREADY_EXISTS);
        }

        boolean isConfirmEmail = confirmEmailRepo.existsByEmailUserAndIsConfirm(email,true);

        // nếu isConfirm = true tức là email này đã xác thực --> Email_already_exists
         if( isConfirmEmail  ){
            throw new DataIntegrityViolationException("Email is already registered");
        }
         String content = confirmEmailService.generateConfirmCode();
         confirmEmailService.sendConfirmEmail(email,content);
         return "Kiểm tra email để lấy mã xác minh tài khoản";
    }

    public void register(RegisterRequest registerRequest){
        Role userRole = roleRepo.findById(1).orElseThrow(()
                -> new IllegalStateException("Role not found with ID 2"));
        User user = User.builder()
                .point(0)
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .userName(registerRequest.getUserName())
                .phoneNumber(registerRequest.getPhoneNumber())
                .isActive(true)
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(userRole)
                .rankcustomer(rankCustomerRepo.findById(1).orElse(null))
                .userStatus(userStatusRepo.findById(1).orElse(null))
                .build();
        userRepo.save(user);
    }
//    public String confirmRegister(RegisterRequest registerRequest, String confirmCode) {
//        try {
//            ConfirmEmail confirmEmail = confirmEmailRepo.findConfirmEmailByConfirmCode(confirmCode);
//            boolean isCofirm = confirmEmailService.confirmEmail(confirmCode);
//            if (!isCofirm){
//                return ResponseEntity.badRequest().body(MessageKeys.ERROR_SERVE);
//            }
//
//            confirmEmail.setUser(null);
//            confirmEmailRepo.delete(confirmEmail);
//            return ResponseEntity.ok().body("Login sucess");
//        } catch (DataNotFoundException ex){
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
//        }catch (ConfirmEmailExpired ex){
//            return ResponseEntity.badRequest().body(ex.getMessage());
//        }catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    public String localDateTimeToString(LocalDateTime localDateTime, int timePlus){

        LocalDateTime expiredTime = localDateTime.plusSeconds(timePlus);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String expiredTimeString = expiredTime.format(formatter);
        return expiredTimeString;
    }

    public String convertLocalDateTime (LocalDateTime localDateTime){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String expiredTimeString = localDateTime.format(formatter);
        return expiredTimeString;
    }

    @Override
    public LoginDTO login(LoginRequest loginRequest) throws Exception {
        User existingUser = userRepo.findByEmail(loginRequest.getEmail()).orElse(null);
        if(existingUser == null) {
            throw new DataNotFoundException(MessageKeys.EMAIL_DOES_NOT_EXISTS);
        }
        if(existingUser.isActive() == false){
            throw new DisabledException(MessageKeys.USER_ACCOUNT_IS_DISABLED);
        }

        //Chuyền email,password, role vào authenticationToken để xac thực ngươi dùng
        UsernamePasswordAuthenticationToken auToken = new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword(),
                existingUser.getAuthorities()
        );
        //Xác thực người dùng (nếu xác thực không thành công VD: sai pass ) thì sẽ ném ra ngoại lệ
        authenticationManager.authenticate(auToken);

        //Lấy role của user
        User userDetails = (User) userDetailsService.loadUserByUsername(loginRequest.getEmail());
        String roles = userDetails.getAuthorities().toString();

        RefreshToken refreshTokens = refreshTokenService.createRefreshToken(existingUser.getId());

        LoginDTO loginDTO= LoginDTO.builder()
                .id(existingUser.getId())
                .userName(existingUser.getUserName())
                .email(existingUser.getEmail())
                .phoneNumber(existingUser.getPhoneNumber())
                .token(refreshTokens.getAccessToken())
                .timeExpiredToken(convertLocalDateTime(refreshTokens.getTimeExpiredAccess()))
                .refreshToken(refreshTokens.getRefreshToken())
                .timeExpiredRefresh(convertLocalDateTime(refreshTokens.getTimeExpiredRefresh()))
                .roles(roles)
                .build();
        return loginDTO;
    }

    @Override
    public String changePassword(String email,ChangePasswordRequest changePasswordRequest) throws Exception {
        User user = userRepo.findByEmail(email).orElse(null);
        if(user == null ){
            throw new DataNotFoundException(MessageKeys.USER_DOES_NOT_EXIST);
        }
        if(changePasswordRequest.getOldPassword().equals(changePasswordRequest.getNewPassword())){
            return "Mật khẩu mới và mật khẩu cũ không được trùng nhau";
        }
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        if(!bCryptPasswordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())){
            return "Mật khẩu cũ không chính xác";
        }
        if(!changePasswordRequest.getConfirmPassword().equals(changePasswordRequest.getNewPassword())){
            return "Xác nhận mật khẩu không trùng khớp";
        }
        user.setPassword(bCryptPasswordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepo.save(user);
        return "Đổi mật khẩu thành công";
    }


    public String generateRandomString() {
        return RandomStringUtils.randomAlphabetic(6);
    }
    @Override
    public String forgotPassword(ForgotPasswordRequest forgotPasswordRequest) throws Exception {
        User user = userRepo.findByEmail(forgotPasswordRequest.getEmail()).orElse(null);
        if(user == null){
            throw new DataNotFoundException(MessageKeys.EMAIL_DOES_NOT_EXISTS);
        }
        String newPassword = generateRandomString();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
        confirmEmailService.sendConfirmEmail(forgotPasswordRequest.getEmail(),newPassword);
        return "Mật khẩu mới đã được gửi vào email của bạn!";
    }
//    public boolean confirmNewPassword(ConfirmNewPasswordRequest confirmNewPasswordRequest){
//        String confirmCode = confirmNewPasswordRequest.getConfirmCode();
//        try {
//            confirmEmailService.confirmEmail(confirmCode);
//
//        } catch (Exception e){
//
//        }
//
//    }

    public User registerRequestToUser(RegisterRequest registerRequest){
        return this.modelMapper.map(registerRequest, User.class);
    }
}
