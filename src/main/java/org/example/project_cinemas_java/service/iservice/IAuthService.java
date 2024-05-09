package org.example.project_cinemas_java.service.iservice;

import org.example.project_cinemas_java.model.User;
import org.example.project_cinemas_java.payload.dto.authdtos.LoginDTO;
import org.example.project_cinemas_java.payload.request.auth_request.ChangePasswordRequest;
import org.example.project_cinemas_java.payload.request.auth_request.ForgotPasswordRequest;
import org.example.project_cinemas_java.payload.request.auth_request.LoginRequest;
import org.example.project_cinemas_java.payload.request.auth_request.RegisterRequest;

public interface IAuthService {
    String confirmEmail(String email) throws Exception;

    LoginDTO login(LoginRequest loginRequest) throws Exception;

    String changePassword(String email, ChangePasswordRequest changePasswordRequest) throws Exception;

    String forgotPassword(ForgotPasswordRequest forgotPasswordRequest)throws Exception;
}
