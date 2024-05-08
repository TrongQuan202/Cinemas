package org.example.project_cinemas_java.payload.dto.cinemadtos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RevenueCinemaDTO {
    private String months;

    private Double revenue;
}
