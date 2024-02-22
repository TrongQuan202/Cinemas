package org.example.project_cinemas_java.service.iservice;

import org.example.project_cinemas_java.model.User;
import org.example.project_cinemas_java.payload.request.auth_request.ConfirmCodeRequest;

public interface IConfirmEmailService {
    void sendConfirmEmail(User user);
    boolean confirmEmail(String confirmCode) throws Exception;
}
