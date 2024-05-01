package org.example.project_cinemas_java.service.implement;

import org.example.project_cinemas_java.components.JwtTokenUtils;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.exceptions.TokenRefreshException;
import org.example.project_cinemas_java.model.RefreshToken;
import org.example.project_cinemas_java.model.User;
import org.example.project_cinemas_java.payload.dto.refreshtokendto.RefreshTokenDTO;
import org.example.project_cinemas_java.repository.RefreshTokenRepo;
import org.example.project_cinemas_java.repository.UserRepo;
import org.example.project_cinemas_java.service.iservice.IRefreshTokenService;
import org.example.project_cinemas_java.utils.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class RefreshTokenService implements IRefreshTokenService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private RefreshTokenRepo refreshTokenRepo;
    @Value("${jwt.expirationRefreshToken}")
    private int expirationRefreshToken;
    @Value("${jwt.expiration}")
    private int expirationAccessToken;
    @Autowired
    private JwtTokenUtils jwtTokenUtils;
    @Override
    public RefreshToken createRefreshToken(int userID) throws Exception {
        User user = userRepo.findById(userID).orElse(null);
        if(user == null){
            throw  new DataNotFoundException(MessageKeys.EMAIL_DOES_NOT_EXISTS);
        }
        if (user.isActive() == false){
            throw new Exception(MessageKeys.USER_ACCOUNT_IS_DISABLED);
        }
        String token = jwtTokenUtils.generateToken(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .accessToken(token)
                .timeExpiredAccess(LocalDateTime.now().plusSeconds(expirationAccessToken))
                .refreshToken(UUID.randomUUID().toString())
                .timeExpiredRefresh(LocalDateTime.now().plusSeconds(expirationRefreshToken))
                .user(user)
                .build();
        refreshToken = refreshTokenRepo.save(refreshToken);
        return refreshToken;
    }

    public String localDateTimeToString(LocalDateTime localDateTime, int timePlus){

        LocalDateTime expiredTime = localDateTime.plusSeconds(timePlus);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String expiredTimeString = expiredTime.format(formatter);
        return expiredTimeString;
    }

    @Override
    public RefreshTokenDTO refreshToken(String refreshToken) throws Exception{
        RefreshToken refresh = refreshTokenRepo.findByRefreshToken(refreshToken);
        if(refresh == null){
            throw new DataNotFoundException("RefreshToken does not exits");
        }
        if (!verifyExpiration(refresh)){
            throw new TokenRefreshException("Refresh token was expired");
        }
        User user = userRepo.findById(refresh.getUser().getId()).orElse(null);
        if(user == null){
            throw  new DataNotFoundException(MessageKeys.USER_DOES_NOT_EXIST);
        }
        String token  = jwtTokenUtils.generateToken(user);

        refresh.setRefreshToken(UUID.randomUUID().toString());
        refresh.setTimeExpiredRefresh(LocalDateTime.now().plusSeconds(expirationRefreshToken));
        refresh.setAccessToken(token);
        refresh.setTimeExpiredAccess(LocalDateTime.now().plusSeconds(expirationAccessToken));

        refreshTokenRepo.save(refresh);
        RefreshTokenDTO refreshTokenDTO = RefreshTokenDTO.builder()
                .refreshToken(refresh.getRefreshToken())
                .timeExpiredRefresh(convertLocalDateTime(refresh.getTimeExpiredRefresh()))
                .token(refresh.getAccessToken())
                .timeExpiredToken(convertLocalDateTime(refresh.getTimeExpiredAccess()))
                .build();

        return refreshTokenDTO;
    }

    public String convertLocalDateTime (LocalDateTime localDateTime){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String expiredTimeString = localDateTime.format(formatter);
        return expiredTimeString;
    }

    public boolean verifyExpiration(RefreshToken token) {
        if (LocalDateTime.now().compareTo(token.getTimeExpiredRefresh()) > 0) {
            token.setUser(null);
            refreshTokenRepo.delete(token);
            return false;
        }

        return true;
    }

}
