package com.jbhunt.finance.carrierpayment.autopay.repository;

import com.jbhunt.finance.carrierpayment.autopay.entity.Parameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ParameterRepository extends JpaRepository<Parameter, Integer>, QuerydslPredicateExecutor<Parameter> {

    List<Parameter> findByWorkflowSpecificationAssociationSpecificationAssociationSpecificationAssociationIDAndAndEffectiveDateIsBeforeAndAndExpirationDateIsAfter(
            @Param("specification") Integer specificationId,
            @Param("effectiveDate") LocalDateTime effective,
            @Param("expirationDate") LocalDateTime expiration);

    List<Parameter> findByWorkflowSpecificationAssociationSpecificationAssociationSpecificationAssociationIDAndWorkflowSpecificationAssociationWorkflowGroupTypeCodeAndAndEffectiveDateIsBeforeAndAndExpirationDateIsAfter(
            @Param("specification") Integer specificationId, @Param("groupType") String groupType, @Param("effectiveDate") LocalDateTime effective,
            @Param("expirationDate") LocalDateTime expiration);

    @Query(value="select count(*) from cfp.CarrierFreightPaymentParameter s left join cfp.ParameterWorkflowSpecificationAssociation p  on s.ParameterWorkflowSpecificationAssociationID = p.ParameterWorkflowSpecificationAssociationID"  +
            " left join cfp.ParameterSpecification r on p.ParameterSpecificationID = r.ParameterSpecificationID where  s.ParameterCharacterValue = 'Dispatch' " +
            " and s.ParameterSpecificationSubTypeCode = :chargeCode and r.ParameterSpecificationID = :parameterSpecificationId and p.CarrierPaymentWorkflowGroupTypeCode =  'LTL'" +
            " and  s.EffectiveTimestamp <= GETDATE() and s.ExpirationTimestamp >= GETDATE()", nativeQuery = true)
    Integer fetchPaymentParameterCount(String chargeCode, Integer parameterSpecificationId);

    @Query(value="  select count(*) from cfp.CarrierFreightPaymentParameter s left join cfp.ParameterWorkflowSpecificationAssociation p  on s.ParameterWorkflowSpecificationAssociationID = p.ParameterWorkflowSpecificationAssociationID" +
            "  left join cfp.ParameterSpecification r on p.ParameterSpecificationID = r.ParameterSpecificationID where  s.ParameterCharacterValue = 'Stop' " +
            "  and s.ParameterSpecificationSubTypeCode = :chargeCode and r.ParameterSpecificationID = :parameterSpecificationId and p.CarrierPaymentWorkflowGroupTypeCode =  'LTL'" +
            "  and  s.EffectiveTimestamp <= GETDATE() and s.ExpirationTimestamp >= GETDATE()", nativeQuery = true)
    Integer fetchPaymentParameterCountForStop(String chargeCode, Integer parameterSpecificationId);
}
