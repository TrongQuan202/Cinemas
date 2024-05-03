package org.example.project_cinemas_java.payload.dto.moviedtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovieByAdminDTO {
    private String movieName;
    private String image;
}
