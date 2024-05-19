package org.example.project_cinemas_java.payload.request;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DeleteByTimeRequest {
    private String start;
    private String end;

}
