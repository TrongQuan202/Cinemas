package org.example.project_cinemas_java.service.implement;

import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.model.RefreshToken;
import org.example.project_cinemas_java.model.User;
import org.example.project_cinemas_java.repository.RefreshTokenRepo;
import org.example.project_cinemas_java.repository.UserRepo;
import org.example.project_cinemas_java.service.iservice.IRefreshTokenService;
import org.example.project_cinemas_java.utils.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenService implements IRefreshTokenService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private RefreshTokenRepo refreshTokenRepo;
    @Value("${jwt.expirationRefreshToken}")
    private int expirationRefreshToken;
    @Override
    public RefreshToken createRefreshToken(int userID) throws Exception {
        User user = userRepo.findById(userID).orElse(null);
        if(user == null){
            throw  new DataNotFoundException(MessageKeys.EMAIL_DOES_NOT_EXISTS);
        }
        if (user.isActive() == false){
            throw new Exception(MessageKeys.USER_ACCOUNT_IS_DISABLED);
        }
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .expiredTime(LocalDateTime.now().plusSeconds(expirationRefreshToken))
                .user(user)
                .build();
        refreshToken = refreshTokenRepo.save(refreshToken);
        return refreshToken;
    }
}
