package com.jbhunt.finance.carrierpayment.autopay.util.payment;

import com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants;
import com.jbhunt.finance.carrierpayment.autopay.dto.ParameterListingDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.CarrierInvoiceDetail;
import com.jbhunt.finance.carrierpayment.autopay.entity.Charge;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class ChargePredicateUtil {

    private ChargePredicateUtil() {

    }

    public static final Predicate<List<Charge>> IS_PAID_AND_REJECT = chargeList -> chargeList.stream()
            .allMatch(val -> val.getChargeDecisionCode().equalsIgnoreCase(CarrierPaymentConstants.PAID)
                    || val.getChargeDecisionCode().equalsIgnoreCase(CarrierPaymentConstants.REJECT_STATUS));

    public static final Predicate<List<Charge>> IS_APPROVE_AND_REJECT = chargeList -> chargeList.stream()
            .allMatch(val -> val.getChargeDecisionCode().equalsIgnoreCase(CarrierPaymentConstants.APPROVE)
                    || val.getChargeDecisionCode().equalsIgnoreCase(CarrierPaymentConstants.REJECT_STATUS));

    public static final Predicate<List<Charge>> IS_APPROVE_PAID_AND_REJECT = chargeList -> chargeList.stream()
            .allMatch(val -> val.getChargeDecisionCode().equalsIgnoreCase(CarrierPaymentConstants.PAID)
                    || val.getChargeDecisionCode().equalsIgnoreCase(CarrierPaymentConstants.REJECT_STATUS)
                    || val.getChargeDecisionCode().equalsIgnoreCase(CarrierPaymentConstants.APPROVE));

    public static final BiPredicate<CarrierInvoiceDetail, List<ParameterListingDTO>> CARRERAPGROUP_EDIT_CHARGES=(carrierInvoiceDetail, parameterAAprChgGrp)->parameterAAprChgGrp.stream().anyMatch( val->val.getSpecificationSub().trim().equalsIgnoreCase(carrierInvoiceDetail.getChargeCode().trim()));

    public static final BiPredicate<Charge, List<ParameterListingDTO>> APGROUP_EDIT_CHARGES=(charge, parameterAAprChgGrp)->parameterAAprChgGrp.stream().anyMatch(val->val.getSpecificationSub().trim().equalsIgnoreCase(charge.getChargeCode().trim()));
    public static final BiPredicate< List<CarrierInvoiceDetail> ,Charge> JBI_OTHER_CHARGES=( tenderedList,charge)->tenderedList.stream().anyMatch(val->(val.getChargeCode().trim().equalsIgnoreCase(charge.getChargeCode().trim())&& val.getCarrierChargeUnitRateAmount().compareTo(charge.getTotalApprovedChargeAmount())==0));
    public static final BiPredicate<String, List<ParameterListingDTO>> APGROUP_APPLYTOCUST_CHARGES_AUTOPAY=(grouptype, parameterListingDTOList)->parameterListingDTOList.stream().anyMatch(val->val.getSpecificationSub().trim().equalsIgnoreCase(grouptype));
    public static final BiPredicate<String, List<CarrierInvoiceDetail> > JBI_ZERO_CHARGES_CHECK=(chargeCode, invoiceList)->invoiceList.stream().anyMatch(x -> x.getChargeCode().equalsIgnoreCase(chargeCode) && x.getCarrierChargeUnitRateAmount().compareTo(BigDecimal.ZERO) == 0);
}
