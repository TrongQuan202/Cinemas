package org.example.project_cinemas_java.payload.dto.moviedtos;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MovieTicketCountDTO {
    private Integer movieId;
    private String nameMovie;
    private Long ticketCount;


}
