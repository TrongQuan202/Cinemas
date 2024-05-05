package org.example.project_cinemas_java.payload.dto.moviedtos;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovieByAdminDTO {
    private String name;

    private int movieDuration;

    private String slug;

    private LocalDateTime endTime;

    private LocalDateTime premiereDate;

    private String description;

    private String director;

    private String image;

    private String herolmage;

    private String language;

    private String trailer;

    private boolean isActive = true;

    private boolean isUpcoming;
}
