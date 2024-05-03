package org.example.project_cinemas_java.payload.dto.authdtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginDTO {
    private Integer id;
    private String userName;
    private String phoneNumber;
    private String email;
    @JsonProperty("token")
    private String token;
    private String timeExpiredToken;
    private String refreshToken;
    private String timeExpiredRefresh;
    private String roles;

}
