package org.example.project_cinemas_java.payload.request.bill_request;

import lombok.*;
import org.springframework.context.annotation.Primary;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateBillRequest {
    private Double totalMoney;
    private String email;
    private String name;
    private String promotion;

}
