package org.example.project_cinemas_java.repository;

import org.example.project_cinemas_java.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface BillRepo extends JpaRepository<Bill, Integer> {
    Bill getBillByTradingCode(String code);
    boolean existsByUser(User user);

    Bill findByUser(User user);

    Bill findBillByUserAndBillstatusIdAndTradingCode(User user,int statusId, String code);
    List<Bill> findAllByUser(User user);

    Bill findBillByUserAndBillstatus(User user, BillStatus billStatus);

    Bill findBillByUserAndBillstatusId(User user, int billStatusId);
    List<Bill> findAllBillByUserAndBillstatusId(User user, int billStatusId);

    List<Bill> findDistinctByBillTicketsIn(List<BillTicket> billTickets);

    Bill findByUserAndBillstatusIdAndPromotionIsNotNull(User user, int billStatusId);

    @Query( nativeQuery = true,
            value = "SELECT MONTH(b.update_time), SUM(b.total_money) " +
                    "FROM cinemalts.bill b WHERE YEAR(b.update_time) = :year AND b.id IN :billIds GROUP BY MONTH(b.update_time)")
    List<Object[]> getMonthlyRevenue(@Param("year") int year,@Param("billIds") List<Integer> billIds);


    @Modifying
    @Transactional
    @Query(value = "UPDATE cinemalts.bill SET is_active = 0 WHERE create_time BETWEEN :startTime AND :endTime", nativeQuery = true)
    void deleteBill(@Param("startTime") String startTime, @Param("endTime") String endTime);

}
