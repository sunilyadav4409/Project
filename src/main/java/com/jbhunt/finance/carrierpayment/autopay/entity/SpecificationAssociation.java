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
@Table(name = "ParameterSpecification", schema = "CFP")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "specificationAssociationID", scope = SpecificationAssociation.class)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@EntityListeners(AuditingEntityListener.class)
public class SpecificationAssociation extends AuditEntity implements Serializable {
    
    private static final long serialVersionUID = -5740118257677679124L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ParameterSpecificationID", unique = true, nullable = false)
    private Integer specificationAssociationID;

    @Column(name = "ParameterSpecificationTypeCode", nullable = false)
    private String specification;
   
    @Column(name = "ParameterSubClassificationTypeCode", nullable = false)
    private String classification;
    
    @Column(name = "ParameterWarningTypeCode", nullable = false)
    private String warning;
    
    @Column(name = "ParameterOwnerTypeCode", nullable = false)
    private String owner;

    @Column(name = "EffectiveTimestamp", nullable = false)
    private LocalDateTime effectiveDate;

    @Column(name = "ExpirationTimestamp", nullable = false)
    private LocalDateTime expirationDate;
}
