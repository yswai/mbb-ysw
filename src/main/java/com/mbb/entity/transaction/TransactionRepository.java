package com.mbb.entity.transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

  Page<Transaction> findByCustomerId(String customerId, Pageable pageable);

  Page<Transaction> findByAccountNumberContainingIgnoreCase(String accountNumber, Pageable pageable);

  @Query("SELECT t FROM Transaction t WHERE LOWER(t.description) LIKE LOWER(CONCAT('%', :description, '%'))")
  Page<Transaction> searchByDescription(@Param("description") String description, Pageable pageable);
}
