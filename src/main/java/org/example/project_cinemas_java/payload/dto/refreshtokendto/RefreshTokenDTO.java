package org.example.project_cinemas_java.payload.dto.refreshtokendto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshTokenDTO {

    private String refreshToken;
    private String timeExpiredRefresh;
    private String token;
    private String timeExpiredToken;
}
