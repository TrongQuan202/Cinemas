package org.example.project_cinemas_java.payload.request.admin_request.cinema_request;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateFoodRequest {
    private double price;

    private String description;

    private String image;

    private String nameOfFood;


}
