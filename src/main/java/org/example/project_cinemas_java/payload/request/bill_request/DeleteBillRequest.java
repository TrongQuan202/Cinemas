package org.example.project_cinemas_java.payload.request.bill_request;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class DeleteBillRequest {
        private String start;
        private String end;
}
