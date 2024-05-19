package org.example.project_cinemas_java.service.iservice;


import org.example.project_cinemas_java.payload.dto.promotiondtos.PromotionOfBillDTO;
import org.example.project_cinemas_java.payload.request.food_request.ChooseFoodRequest;

public interface IBillFoodService {
    void createBillFood(int foodId,String email)throws  Exception;

    void removeBillFood(int foodId, String email) throws Exception;

    PromotionOfBillDTO chooseFood(String email, ChooseFoodRequest chooseFoodRequest) throws Exception;
}
