package org.example.project_cinemas_java.payload.dto.rankcustomerdtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RankUserDTO {
    private String name;
    private String description;
    private int point;
}
