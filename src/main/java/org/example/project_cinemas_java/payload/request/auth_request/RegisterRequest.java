package org.example.project_cinemas_java.payload.request.auth_request;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterRequest {
    private String userName;
    private String name;
    private String email;
    private String password;
    private String retypePassword;
    private String phoneNumber;
}
