package org.example.project_cinemas_java.payload.dto.moviedtos;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovieSuggestDTO {

    private String image;
    private String slug;
    private String movieName;
}
