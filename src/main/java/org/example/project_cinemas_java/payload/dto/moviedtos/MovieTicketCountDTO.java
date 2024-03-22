package org.example.project_cinemas_java.payload.dto.moviedtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovieTicketCountDTO {
    private Integer movieId;
    private String movieName;
    private String movieImage;
    private Integer movieDuration;
    private String movieTrailer;
    private String movieTypeName;
    private String movieDescription;
    private String movieDirector;
    private String movieLanguage;
    private String moviePremiereDate;
}
