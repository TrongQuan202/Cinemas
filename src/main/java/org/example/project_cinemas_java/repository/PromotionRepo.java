package org.example.project_cinemas_java.repository;

import org.example.project_cinemas_java.model.Promotion;
import org.example.project_cinemas_java.model.RankCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromotionRepo extends JpaRepository<Promotion, Integer> {

    List<Promotion> findAllByRankcustomer(RankCustomer rankCustomer);

    Promotion findByName(String name);

    Promotion findByCode(String code);
}
