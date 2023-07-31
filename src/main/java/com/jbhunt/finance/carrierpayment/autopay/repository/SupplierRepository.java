package com.jbhunt.finance.carrierpayment.autopay.repository;

import com.jbhunt.finance.carrierpayment.autopay.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Integer> {

    Supplier findByScacCodeAndDefaultCurrencyAndExpirationTimestampGreaterThan(String sCACCode,String currencyCode, LocalDateTime expirationTimestamp);

    Supplier findBySupplierID(Integer parentSupplierID);


}
