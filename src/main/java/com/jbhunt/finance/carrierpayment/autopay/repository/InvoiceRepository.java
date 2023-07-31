package com.jbhunt.finance.carrierpayment.autopay.repository;

import com.jbhunt.finance.carrierpayment.autopay.entity.CarrierInvoiceHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RepositoryRestResource(path = "invoices")
public interface InvoiceRepository extends JpaRepository<CarrierInvoiceHeader, Integer> {

    CarrierInvoiceHeader findByCarrierInvoiceHeaderId(Integer carrierInvoiceHeaderId);

    CarrierInvoiceHeader findTopByCarrierPaymentCarrierPaymentIdAndInvoiceSourceTypeCodeNotAndInvoiceStatusCodeNotOrderByCarrierInvoiceHeaderIdDesc(
            @Param("paymentId") Integer paymentId, @Param("invoiceSourceTypeCode") String invoiceSourceTypeCode,
            @Param("invoiceStatusCode") String invoiceStatusCode);
    @Query(value = "select count (1)  from cfp.CarrierPayment p, cfp.CarrierPaymentCharge c " +
            "where p.SCACCode =:scac  and p.CarrierPaymentID = c.CarrierPaymentID " +
            "and c.ExpirationTimestamp is null and  c.CarrierInvoiceNumberExtended = :carrierInvoiceNumberExtended",nativeQuery = true)
    Integer retrieveApprovedIvcByScacAndCarrierInvoiceNumberExtended( @Param("scac") String scac, @Param("carrierInvoiceNumberExtended") String carrierInvoiceNumberExtended);
    List<CarrierInvoiceHeader> findByCarrierPaymentCarrierPaymentIdAndInvoiceSourceTypeCodeNotAndInvoiceStatusCodeNotOrderByCarrierInvoiceHeaderIdDesc(
            @Param("paymentId") Integer paymentId, @Param("invoiceSourceTypeCode") String invoiceSourceTypeCode,@Param("invoiceStatusCode") String invoiceStatusCode);

    List<CarrierInvoiceHeader> findByCarrierPaymentCarrierPaymentIdOrderByCarrierInvoiceHeaderIdDesc(
            @Param("paymentId") Integer paymentId);
    CarrierInvoiceHeader findTopByCarrierPaymentCarrierPaymentIdAndInvoiceStatusCodeNotOrderByCarrierInvoiceHeaderIdDesc(
            @Param("paymentId") Integer paymentId,
            @Param("invoiceStatusCode") String invoiceStatusCode);
}
