package org.example.project_cinemas_java.payload.dto.moviedtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RateDTO {
    private Float vote;
    private Integer totalVote;
}
