package org.example.project_cinemas_java.repository;

import org.example.project_cinemas_java.model.BillStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillStatusRepo extends JpaRepository<BillStatus, Integer> {
}
