package org.example.project_cinemas_java.payload.request.auth_request;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
