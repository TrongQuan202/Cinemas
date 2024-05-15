package org.example.project_cinemas_java.payload.dto.billdtos;

import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BillAdminDTO {
    private String tradingCode;
    private String name;

    private int status;

    private String voucher;
    private String user;
    private int month;

    private double totalMoney;


    private String createTime;
}
