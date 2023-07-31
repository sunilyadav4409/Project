package com.jbhunt.finance.carrierpayment.autopay.repository;

import com.jbhunt.finance.carrierpayment.autopay.entity.ApprovalTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalTransactionRepository extends JpaRepository<ApprovalTransaction, Integer> {
}
