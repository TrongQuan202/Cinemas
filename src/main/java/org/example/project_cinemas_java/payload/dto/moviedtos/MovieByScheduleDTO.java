package org.example.project_cinemas_java.payload.dto.moviedtos;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovieByScheduleDTO {
    private String img;
    private String movieName;
    private Set<String> movieType;
    private int duration;
    private String cinemaName;
    private String day;
    private String startAt;
    private String romName;
}
