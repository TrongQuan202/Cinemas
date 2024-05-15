package org.example.project_cinemas_java.service.iservice;

import org.example.project_cinemas_java.payload.dto.billdtos.BillAdminDTO;
import org.example.project_cinemas_java.payload.dto.billdtos.BillDTO;
import org.example.project_cinemas_java.payload.request.bill_request.CreateBillRequest;

import java.util.List;

public interface IBillService {
    void createBill(String email)throws Exception;

    String saveBillInformation(int user)throws Exception;

    void resetBillByUser(int user) throws Exception;

    List<BillAdminDTO> getAllBillAdmin() throws Exception;
}
