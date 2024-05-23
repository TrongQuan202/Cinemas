package org.example.project_cinemas_java.payload.request.promotion_request;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreatePromotionRequest {
    private String start;
    private String end;
    private String name;
    private String rank;
    private int percent;
    private int quantity;
    private String image;
    private String content;

}
