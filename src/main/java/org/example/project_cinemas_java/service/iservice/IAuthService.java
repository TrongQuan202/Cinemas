package org.example.project_cinemas_java.service.iservice;

import org.example.project_cinemas_java.model.User;
import org.example.project_cinemas_java.payload.request.auth_request.RegisterRequest;

public interface IAuthService {
    User register(RegisterRequest registerRequest) throws Exception;

}
