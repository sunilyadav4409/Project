package com.jbhunt.finance.carrierpayment.autopay.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ParameterWorkflowSpecificationAssociation", schema = "CFP")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "workflowSpecificationAssociationID", scope = WorkflowSpecificationAssociation.class)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@EntityListeners(AuditingEntityListener.class)
public class WorkflowSpecificationAssociation extends AuditEntity implements Serializable {

    private static final long serialVersionUID = -5740118257677679124L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ParameterWorkflowSpecificationAssociationID", unique = true, nullable = false)
    private Integer workflowSpecificationAssociationID;

    @ManyToOne
    @JoinColumn(name = "ParameterSpecificationID", nullable = false)
    private SpecificationAssociation specificationAssociation;
    
    @Column(name = "CarrierPaymentWorkflowGroupTypeCode", nullable = false)
    private String workflowGroupTypeCode;

    @Column(name = "EffectiveTimestamp", nullable = false)
    private LocalDateTime effectiveDate;

    @Column(name = "ExpirationTimestamp", nullable = false)
    private LocalDateTime expirationDate;
}
