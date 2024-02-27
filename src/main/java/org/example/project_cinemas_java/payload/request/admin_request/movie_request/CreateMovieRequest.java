package org.example.project_cinemas_java.payload.request.admin_request.movie_request;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateMovieRequest {
    private int movieDuration;

    private String  endTime;

    private String premiereDate;

    private String description;

    private String director;

    private String image;

    private String herolmage;

    private String language;

    private String name;

    private String trailer;

    private int movieTypeId;

    private int rateId;

}
