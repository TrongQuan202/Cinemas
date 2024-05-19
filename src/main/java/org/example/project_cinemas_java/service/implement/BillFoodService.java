package org.example.project_cinemas_java.service.implement;

import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.model.Bill;
import org.example.project_cinemas_java.model.BillFood;
import org.example.project_cinemas_java.model.Food;
import org.example.project_cinemas_java.model.User;
import org.example.project_cinemas_java.payload.dto.promotiondtos.PromotionOfBillDTO;
import org.example.project_cinemas_java.payload.request.food_request.ChooseFoodRequest;
import org.example.project_cinemas_java.repository.*;
import org.example.project_cinemas_java.service.iservice.IBillFoodService;
import org.example.project_cinemas_java.utils.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BillFoodService implements IBillFoodService {

    @Autowired
    private BillRepo billRepo;
    @Autowired
    private FoodRepo foodRepo;
    @Autowired
    private BillFoodRepo billFoodRepo;
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BillStatusRepo billStatusRepo;
    @Override
    public void createBillFood(int foodId, String email) throws Exception {
        User user = userRepo.findByEmail(email).orElse(null);
        if(user == null){
            throw new DataNotFoundException(MessageKeys.USER_DOES_NOT_EXIST);
        }

        Bill bill = billRepo.findByUser(user);
        if(bill == null){
            throw new DataNotFoundException("Bill does not exits");
        }

        Food food = foodRepo.findById(foodId).orElse(null);
        if(food == null){
            throw new DataNotFoundException(MessageKeys.FOOD_DOES_NOT_EXIST);
        }

        BillFood billFood = billFoodRepo.findByBillAndFood(bill,food);
        if(billFood == null){
            BillFood billFood1 = new BillFood();
            billFood1.setFood(food);
            billFood1.setBill(bill);
            billFood1.setQuantity(1);
            billFoodRepo.save(billFood1);
        }else{
            billFood.setQuantity(billFood.getQuantity() + 1);
            billFoodRepo.save(billFood);
        }
    }

    @Override
    public void removeBillFood(int foodId, String email) throws Exception {
        User user = userRepo.findByEmail(email).orElse(null);
        if(user == null){
            throw new DataNotFoundException(MessageKeys.USER_DOES_NOT_EXIST);
        }

        Bill bill = billRepo.findByUser(user);
        if(bill == null){
            throw new DataNotFoundException("Bill does not exits");
        }

        Food food = foodRepo.findById(foodId).orElse(null);
        if(food == null){
            throw new DataNotFoundException(MessageKeys.FOOD_DOES_NOT_EXIST);
        }

        BillFood billFood = billFoodRepo.findByBillAndFood(bill,food);
        billFood.setQuantity(billFood.getQuantity()-1);
        billFoodRepo.save(billFood);

        if(billFood.getQuantity() == 0){
            billFood.setBill(null);
            billFood.setFood(null);
            billFoodRepo.delete(billFood);
        }
    }

    @Override
    public PromotionOfBillDTO chooseFood(String email, ChooseFoodRequest chooseFoodRequest) throws Exception {
        User user = userRepo.findByEmail(email).orElse(null);
        if(user == null){
            throw  new DataNotFoundException(MessageKeys.USER_DOES_NOT_EXIST);
        }
        Food food = foodRepo.findById(chooseFoodRequest.getFoodId()).orElse(null);
        if(food == null){
            throw new DataNotFoundException(MessageKeys.FOOD_ALREADY_EXIST);
        }
        Bill bill = billRepo.findBillByUserAndBillstatus(user, billStatusRepo.findById(3).orElse(null));
        if(bill ==null){
            throw new DataNotFoundException("Đơn hàng không tồn tại");
        }
        PromotionOfBillDTO promotionOfBillDTO = new PromotionOfBillDTO();
        //trường hợp chọn đồ ăn
        if(chooseFoodRequest.getChooseFood() == 1) {
            BillFood billFood = billFoodRepo.findByBillAndFood(bill,food);
            if(billFood == null) {
                BillFood billFood1 = new BillFood();
                billFood1.setFood(food);
                billFood1.setBill(bill);
                billFood1.setQuantity(1);
                billFoodRepo.save(billFood1);
                bill.setTotalMoney(bill.getTotalMoney() + billFood1.getFood().getPrice());
                billRepo.save(bill);
                promotionOfBillDTO.setTotalMoney(bill.getTotalMoney());
                //nếu đã áp dụng mã giảm gía thì tính discountAmount
                promotionOfBillDTO.setDiscountAmount(bill.getPromotion() != null ? bill.getTotalMoney() * ((double) bill.getPromotion().getPercent() / 100) : 0);
                promotionOfBillDTO.setFinalAmount(bill.getTotalMoney() - promotionOfBillDTO.getDiscountAmount());
            }else {
                billFood.setQuantity(billFood.getQuantity() + 1);
                billFoodRepo.save(billFood);
                bill.setTotalMoney(bill.getTotalMoney() + billFood.getFood().getPrice());
                billRepo.save(bill);
                promotionOfBillDTO.setTotalMoney(bill.getTotalMoney());
                promotionOfBillDTO.setDiscountAmount(bill.getPromotion() != null ? bill.getTotalMoney() * ((double) bill.getPromotion().getPercent() / 100) : 0);
                promotionOfBillDTO.setFinalAmount(bill.getTotalMoney() - promotionOfBillDTO.getDiscountAmount());
            }

        }
        //trường hợp remove
        if(chooseFoodRequest.getChooseFood() == 2) {
            BillFood billFood = billFoodRepo.findByBillAndFood(bill,food);
            if(billFood == null){
                throw new DataNotFoundException("Combo chưa được chọn");
            }
            billFood.setQuantity(billFood.getQuantity() -1);
            bill.setTotalMoney(bill.getTotalMoney() - billFood.getFood().getPrice());
            billRepo.save(bill);
            promotionOfBillDTO.setTotalMoney(bill.getTotalMoney());
            promotionOfBillDTO.setDiscountAmount(bill.getPromotion() != null ? bill.getTotalMoney() * ((double) bill.getPromotion().getPercent() / 100) : 0);
            promotionOfBillDTO.setFinalAmount(bill.getTotalMoney() - promotionOfBillDTO.getDiscountAmount());
        }

        return promotionOfBillDTO;
    }
}
