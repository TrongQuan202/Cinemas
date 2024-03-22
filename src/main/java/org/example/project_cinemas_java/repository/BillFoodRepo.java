package org.example.project_cinemas_java.repository;

import org.example.project_cinemas_java.model.Bill;
import org.example.project_cinemas_java.model.BillFood;
import org.example.project_cinemas_java.model.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillFoodRepo extends JpaRepository<BillFood, Integer> {

    BillFood findByBillAndFood(Bill bill, Food food);
}
