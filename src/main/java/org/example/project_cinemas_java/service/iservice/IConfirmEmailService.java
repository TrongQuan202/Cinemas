package org.example.project_cinemas_java.service.iservice;

import org.example.project_cinemas_java.model.User;

public interface IConfirmEmailService {
    void sendConfirmEmail(String email);
    boolean confirmEmail(String confirmCode) throws Exception;
}
