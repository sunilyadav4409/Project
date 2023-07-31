package com.jbhunt.finance.carrierpayment.autopay.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "DocumentOverride", schema = "CFP")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "DocumentOverrideID", scope = DocumentOverride.class)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@EntityListeners(AuditingEntityListener.class)
public class DocumentOverride extends AuditEntity implements Serializable{

	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "DocumentOverrideID", unique = true, nullable = false)
	private Integer documentOverrideID;
	
	@ManyToOne
	@JoinColumn(name="CarrierRequiredDocumentID",nullable = false)
	private RequiredDocument carrierRequiredDocumentID;
	
	@Column(name="DocumentOverrideUserID")
	private String userId;
	
	@Column(name="DocumentOverideReasonCode",nullable = false)
	private String overridereasonCode;
	
	@Column(name="DocumentOverrideComment",nullable = true)
	private String overrideComment;
}
