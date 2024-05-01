package org.example.project_cinemas_java.service.iservice;

import org.example.project_cinemas_java.model.RefreshToken;
import org.example.project_cinemas_java.model.User;
import org.example.project_cinemas_java.payload.dto.refreshtokendto.RefreshTokenDTO;

public interface IRefreshTokenService {
    RefreshToken createRefreshToken (int userID) throws Exception;

    RefreshTokenDTO refreshToken(String refreshToken) throws Exception;
}
