package org.example.project_cinemas_java.service.iservice;

import org.example.project_cinemas_java.model.Bill;
import org.example.project_cinemas_java.model.User;
import org.example.project_cinemas_java.payload.dto.billdtos.BillAdminDTO;
import org.example.project_cinemas_java.payload.dto.billdtos.BillDTO;
import org.example.project_cinemas_java.payload.dto.billdtos.HistoryBillByUserDTO;
import org.example.project_cinemas_java.payload.request.bill_request.CreateBillRequest;
import org.example.project_cinemas_java.payload.request.bill_request.DeleteBillRequest;

import java.util.List;

public interface IBillService {
    void createBill(String email)throws Exception;

    String saveBillInformation(int user, float finalAmount)throws Exception;

    void resetBillByUser(int user) throws Exception;

    List<BillAdminDTO> getAllBillAdmin() throws Exception;

    void deleteBill(DeleteBillRequest deleteBillRequest) throws Exception;

    List<HistoryBillByUserDTO> getAllHistoryBillByUser(String email) throws Exception;

}
