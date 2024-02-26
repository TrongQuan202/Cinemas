package org.example.project_cinemas_java.payload.request.auth_request;

import lombok.*;
import org.example.project_cinemas_java.model.User;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ConfirmRegisterRequest {
    private String confirmCode;
    private RegisterRequest registerRequest;
}
