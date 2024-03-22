package org.example.project_cinemas_java.repository;

import org.example.project_cinemas_java.model.RankCustomer;
import org.example.project_cinemas_java.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RankCustomerRepo extends JpaRepository<RankCustomer, Integer> {
    RankCustomer findByUsers(User user);
}
