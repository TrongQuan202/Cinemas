package org.example.project_cinemas_java.payload.dto.promotiondtos;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PromotionByAdminDTO {

    private String image;
    private String name;
    private String code;
    private int percent;

    private int quantity;

    private String startTime;

    private String endTime;

    private boolean isActive = true;
}
