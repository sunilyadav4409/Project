package com.jbhunt.finance.carrierpayment.autopay.repository;

import com.jbhunt.finance.carrierpayment.autopay.entity.SpecificationAssociation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface SpecificationAssociationRepository
        extends JpaRepository<SpecificationAssociation, Integer>, QuerydslPredicateExecutor<SpecificationAssociation> {

    SpecificationAssociation findBySpecificationAndClassificationAndOwnerAndEffectiveDateIsBeforeAndExpirationDateIsAfter(@Param("specification") String specification,
                                                                          @Param("classification") String classification, @Param("owner") String owner,
                                                                                                                          @Param("effectiveDate") LocalDateTime effective,
                                                                                                                          @Param("expirationDate") LocalDateTime expiration);

    @Query(value = "select ParameterSpecificationID from cfp.ParameterSpecification where ParameterSpecificationTypeCode = :specTypeCode and " +
            "ParameterSubClassificationTypeCode = :classification and ParameterOwnerTypeCode = :owner and  EffectiveTimestamp <= GETDATE() and ExpirationTimestamp >= GETDATE()",nativeQuery = true)
    Integer findSpecificationAssociationIDForSpecificationChargeLevel(String specTypeCode, String classification, String owner);

}
