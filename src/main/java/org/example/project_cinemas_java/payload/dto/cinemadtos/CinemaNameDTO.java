package org.example.project_cinemas_java.payload.dto.cinemadtos;

import lombok.*;
import org.hibernate.query.sql.internal.ParameterRecognizerImpl;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class CinemaNameDTO {
    private String name;
}
