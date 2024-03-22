package org.example.project_cinemas_java.payload.request.ticket_request;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookTicketRequest {
   private String startTime;
   private String dayMonthYear;
   private Integer movieId;
   private Integer roomId;
   private Integer seatId;
   private String email;
}
