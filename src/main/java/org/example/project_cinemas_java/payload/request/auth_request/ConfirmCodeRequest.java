package org.example.project_cinemas_java.payload.request.auth_request;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ConfirmCodeRequest {
    private String confirmCode;
}
