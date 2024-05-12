package org.example.project_cinemas_java.payload.dto.fooddtos;

import lombok.*;
import org.hibernate.query.sql.internal.ParameterRecognizerImpl;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChooseFoodDTO {
    private int foodPlus;
    private int foodMinus;
}
