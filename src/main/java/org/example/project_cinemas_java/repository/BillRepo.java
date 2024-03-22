package org.example.project_cinemas_java.repository;

import org.example.project_cinemas_java.model.Bill;
import org.example.project_cinemas_java.model.BillStatus;
import org.example.project_cinemas_java.model.BillTicket;
import org.example.project_cinemas_java.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillRepo extends JpaRepository<Bill, Integer> {
    Bill getBillByTradingCode(String code);
    boolean existsByUser(User user);

    Bill findByUser(User user);

    Bill findBillByUserAndBillstatus(User user, BillStatus billStatus);

    List<Bill> findDistinctByBillTicketsIn(List<BillTicket> billTickets);

    @Query( nativeQuery = true,
            value = "SELECT MONTH(b.update_time), SUM(b.total_money) " +
                    "FROM cinemalts.bill b WHERE YEAR(b.update_time) = :year AND b.id IN :billIds GROUP BY MONTH(b.update_time)")
    List<Object[]> getMonthlyRevenue(@Param("year") int year,@Param("billIds") List<Integer> billIds);

}
