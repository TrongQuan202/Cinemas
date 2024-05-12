package org.example.project_cinemas_java.payload.request.food_request;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChooseFoodRequest {
    private int foodId;
    private int chooseFood;
}
