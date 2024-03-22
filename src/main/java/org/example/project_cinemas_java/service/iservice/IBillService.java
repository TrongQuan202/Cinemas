package org.example.project_cinemas_java.service.iservice;

import org.example.project_cinemas_java.payload.dto.billdtos.BillDTO;
import org.example.project_cinemas_java.payload.request.bill_request.CreateBillRequest;

public interface IBillService {
    void createBill(String email)throws Exception;
}
