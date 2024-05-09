package org.example.project_cinemas_java.payload.request.auth_request;

import lombok.Data;

@Data
public class ChangePasswordRequest {

    private String oldPassword;
    private String newPassword;
    private String confirmPassword;

}
