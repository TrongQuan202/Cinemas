package org.example.project_cinemas_java.payload.request.admin_request.movie_request;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateMovieRequest {
    private int movieId;
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


}
