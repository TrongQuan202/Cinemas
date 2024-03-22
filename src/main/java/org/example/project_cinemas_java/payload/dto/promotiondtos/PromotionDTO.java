package org.example.project_cinemas_java.payload.dto.promotiondtos;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PromotionDTO {
    private String name;
    private String description;
    private Integer quantity;
    private String endTime;
}
