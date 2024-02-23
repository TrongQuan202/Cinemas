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
    @JsonProperty("token")
    private String token;
    private String refreshToken;
    private List<String> roles;

}
