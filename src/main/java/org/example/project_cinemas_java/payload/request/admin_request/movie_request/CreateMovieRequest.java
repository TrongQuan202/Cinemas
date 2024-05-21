package org.example.project_cinemas_java.payload.request.admin_request.movie_request;

import lombok.*;
import org.example.project_cinemas_java.payload.request.movie_request.ActorRequest;
import org.example.project_cinemas_java.payload.request.movie_request.MovieTypeRequest;

import java.time.LocalDateTime;
import java.util.List;

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

    private String heroImage;
    private String imageSuggest;

    private String language;

    private String name;

    private String trailer;

    private List<MovieTypeRequest> type;

    private List<ActorRequest> actor;

    private String slug;

    private String codeCinema;

    private String isUpcoming;


}
