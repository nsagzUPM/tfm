package com.upm.library.repository;

import com.upm.library.domain.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    Optional<Loan> findByUserIdAndCopyIdAndClosed(Long userId, Long copyId, boolean closed);
    List<Loan> findByUserIdAndClosed(Long userId, boolean closed);
    Optional<Loan> findByCopyIdAndClosed(Long copyId, boolean closed);
    @Query("""
       select l.copy.id
       from Loan l
       where l.user.id = :userId
         and l.returnDate is null
         and l.copy.id in :copyIds
       """)
    List<Long> findActiveCopyIdsByUserIdAndCopyIdIn(Long userId, List<Long> copyIds);
}
