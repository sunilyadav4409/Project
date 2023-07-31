package com.jbhunt.finance.carrierpayment.autopay.repository;


import com.jbhunt.finance.carrierpayment.autopay.entity.WorkflowGroupAssociation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowGroupAssociationRepository
        extends JpaRepository<WorkflowGroupAssociation, Integer>, QuerydslPredicateExecutor<WorkflowGroupAssociation> {

    WorkflowGroupAssociation findByWorkflowGroupTypeCodeAndWorkflowStatusEventCode(
            @Param("workflowGroupTypeCode") String workflowGroupTypeCode,
            @Param("workflowStatusEventCode") String workflowStatusEventCode);

}
