package org.example.project_cinemas_java.payload.request.admin_request.cinema_request;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateCinemaRequest {
    private String address;
    private String description;

    private String  code;
    private String nameOfCinema;
}
