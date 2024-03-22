package org.example.project_cinemas_java.service.iservice;

public interface IBillFoodService {
    void createBillFood(int foodId,String email)throws  Exception;

    void removeBillFood(int foodId, String email) throws Exception;
}
