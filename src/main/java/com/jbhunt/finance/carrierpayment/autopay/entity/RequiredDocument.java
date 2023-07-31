package com.jbhunt.finance.carrierpayment.autopay.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "CarrierRequiredDocument", schema = "CFP")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "carrierRequiredDocumentID", scope = RequiredDocument.class)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@EntityListeners(AuditingEntityListener.class)
public class RequiredDocument extends AuditEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CarrierRequiredDocumentID", unique = true, nullable = false)
	private Integer carrierRequiredDocumentID;

	@ManyToOne
	@JoinColumn(name = "CarrierPaymentID", nullable = false)
	private Payment paymentId;

	@Column(name = "DocumentTypeCode")
	private String documentTypeCode;

	@Column(name = "DocumentRequirementFulfillmentDate")
	private LocalDateTime documentRequirementFulfillmentDate;

	@Column(name = "DocumentReceivedDate")
	private LocalDateTime documentRecievedDate;

	@Column(name = "CarrierDocumentNumber")
	private String documentNumber;

	@Column(name = "RequirementTypeCode")
	private String requirementTypeCode;

	@Column(name = "ScanTimestamp")
	private LocalDateTime documentScanDate;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "carrierRequiredDocumentID", cascade = CascadeType.ALL)
	private List<DocumentOverride> documentOverride;
}
