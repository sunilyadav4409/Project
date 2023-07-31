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
@Table(name = "CarrierPaymentWorkflowGroupAssociation", schema = "CFP")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "carrierPaymentOptimisticLockId", scope = WorkflowGroupAssociation.class)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@EntityListeners(AuditingEntityListener.class)
public class WorkflowGroupAssociation extends AuditEntity implements Serializable {
	
	private static final long serialVersionUID = 8131926014192562233L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CarrierPaymentWorkflowGroupAssociationID", unique = true, nullable = false)
    private Integer groupAssociationID;
	
	@Column(name = "CarrierPaymentWorkflowStatusTypeCode", nullable = false)
	private String workflowStatusTypeCode;
	
	@Column(name = "CarrierPaymentWorkflowGroupTypeCode", nullable = false)
	private String workflowGroupTypeCode;
	
	@Column(name = "CarrierPaymentWorkflowStatusEventCode", nullable = false)
	private String workflowStatusEventCode;
	
	@Column(name = "EffectiveTimestamp", nullable = false)
	private LocalDateTime effectiveTimestamp;

	@Column(name = "ExpirationTimestamp", nullable = false)
	private LocalDateTime expirationTimestamp;
}
