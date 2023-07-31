package com.jbhunt.finance.carrierpayment.autopay.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "CarrierFreightPaymentParameter", schema = "CFP")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "parameterID", scope = Parameter.class)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@EntityListeners(AuditingEntityListener.class)
public class Parameter extends AuditEntity implements Serializable {

	private static final long serialVersionUID = -5740118257677679124L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CarrierFreightPaymentParameterID", unique = true, nullable = false)
	private Integer parameterID;

	@ManyToOne
	@JoinColumn(name = "ParameterWorkflowSpecificationAssociationID", nullable = false)
	private WorkflowSpecificationAssociation workflowSpecificationAssociation;
	
	@Column(name = "LogicalOperatorCode")
    private String operatorCode;

	@Column(name = "ParameterSpecificationSubTypeCode", nullable = false)
	private String specificationSub;
	
	@Column(name = "PrimaryParameterNumberValue", nullable = false)
	private BigDecimal minNumberValue;
	
	@Column(name = "SecondaryParameterNumberValue")
	private BigDecimal maxNumberValue;
	
	@Column(name = "EffectiveTimestamp", nullable = false)
	private LocalDateTime effectiveDate;

	@Column(name = "ExpirationTimestamp", nullable = false)
	private LocalDateTime expirationDate;

	@Column(name = "ParameterCharacterValue")
	private String parameterCharacterValue;

}
