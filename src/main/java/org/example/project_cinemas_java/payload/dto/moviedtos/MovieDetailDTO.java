package org.example.project_cinemas_java.payload.dto.moviedtos;

import lombok.*;
import org.example.project_cinemas_java.payload.dto.scheduledtos.ScheduleDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovieDetailDTO {
    private String name;

    private String trailer;

    private String image;
    private String heroImage;

    private Integer duration;

    private String description;

    private LocalDate startDate;

    private Float vote;

    private Integer totalVote;

    private String language;

    private Set<String> movieType;

    private String directors;

    private List<String> actors;
}
