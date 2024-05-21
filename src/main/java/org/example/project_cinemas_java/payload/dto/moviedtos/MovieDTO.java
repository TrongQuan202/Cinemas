package org.example.project_cinemas_java.payload.dto.moviedtos;

import lombok.*;
//
//import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovieDTO {
    private String movieName;
    private String movieImage;
    private Integer movieDuration;
    private String movieTrailer;
    private String slug;
    private Set<String> movieTypeName;
    private boolean isUpcoming;


}
