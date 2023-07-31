package com.jbhunt.finance.carrierpayment.autopay.service.payment;

import com.jbhunt.finance.carrierpayment.autopay.constants.CarrierPaymentConstants;
import com.jbhunt.finance.carrierpayment.autopay.dto.ChargeDTO;
import com.jbhunt.finance.carrierpayment.autopay.dto.ParameterListingDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.*;
import com.jbhunt.finance.carrierpayment.autopay.util.payment.ChargePredicateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.jbhunt.finance.carrierpayment.autopay.util.payment.CarrierPaymentPredicateUtil.IS_GREATER;
import static com.jbhunt.finance.carrierpayment.autopay.util.payment.CarrierPaymentPredicateUtil.IS_GREATER_AUTO_PAY;


@Slf4j
@Service
public class AutoPayEditsService {

    @Autowired
    private ChargeDecisionService chargeDecisionService;

    @Autowired
    private AutoPayService autoPayService;

    @Autowired
    private AutopayValidationService autopayValidationService;

    @Autowired
    private PaymentHelperService paymentHelperService;



    public boolean checkInvoiceRulesForAutopay(Payment payment, CarrierInvoiceHeader ediInvoice, List<ParameterListingDTO> parameterListings) {

        log.info("checkInvoiceRulesForAutopay edits method started ");
        List<CarrierInvoiceDetail> invoiceDetail = ediInvoice.getCarrierInvoiceDetailList();
        boolean validToUpdate = true;
        List<ParameterListingDTO> parameterAutoApprv = autoPayService.getRequiredParameterList( parameterListings, CarrierPaymentConstants.SPECIFICATION_AUTOPAY_CHARGEGROUP );

        List<Charge> unActionedChargeList = Optional.ofNullable(payment.getCarrierPaymentChargeList())
                .map(chargeList -> chargeList.stream()
                        .filter(expTimeNull -> expTimeNull.getExpirationTimestamp() == null)
                        .filter(decision -> decision.getChargeDecisionCode() == null
                        ).collect(Collectors.toList())).orElse(new ArrayList<>());
        ArrayList<Charge> grpchargeList=new ArrayList<>();
        ArrayList<Charge> othrchargeList=new ArrayList<>();
        ArrayList<CarrierInvoiceDetail> invoiceDetalgrpList=new ArrayList<>();
        ArrayList<CarrierInvoiceDetail> othrinvoiceDetalList=new ArrayList<>();
        unActionedChargeList.forEach(charge->
                getChargeList( parameterAutoApprv, grpchargeList, othrchargeList, charge )
        );
        Optional.ofNullable(invoiceDetail).filter( list -> !list.isEmpty() )
                .ifPresent(invoiceDetailList1 ->
                        invoiceDetailList1.forEach(invoiceDetal->
                                getInvoiceDetailList( parameterAutoApprv, invoiceDetalgrpList, othrinvoiceDetalList, invoiceDetal )));
        BigDecimal invoicegroupamount =invoiceDetalgrpList.stream().map(CarrierInvoiceDetail::getCarrierChargeUnitRateAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal chrgegroupamount =grpchargeList.stream().map(Charge::getTotalApprovedChargeAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totlchrgeamt = payment.getCarrierPaymentChargeList().stream().filter(expTimeNull -> expTimeNull.getExpirationTimestamp() == null)
                .filter(decision -> decision.getChargeDecisionCode() == null).map(Charge::getTotalApprovedChargeAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalinvoicegroupamount =invoiceDetail.stream().map(CarrierInvoiceDetail::getCarrierChargeUnitRateAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        // New Change - If FuelSurchg is there at Invoice Side without having Transit At Invoice Side then NO Auto Pay Starts
        Optional<CarrierInvoiceDetail> transitInvCharge=invoiceDetalgrpList.stream().filter(val3->val3.getChargeCode().trim().equalsIgnoreCase(CarrierPaymentConstants.TRANSIT)).findAny();
        Optional<CarrierInvoiceDetail> fuelInvCharge=invoiceDetalgrpList.stream().filter(val3->val3.getChargeCode().trim().equalsIgnoreCase(CarrierPaymentConstants.FUELSURCHG)).findAny();

        BigDecimal multransitInvChargeamt=invoiceDetalgrpList.stream().filter(val3->val3.getChargeCode().trim().equalsIgnoreCase(CarrierPaymentConstants.TRANSIT)).map(CarrierInvoiceDetail::getCarrierChargeUnitRateAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal mulfuelInvChargeamt=invoiceDetalgrpList.stream().filter(val3->val3.getChargeCode().trim().equalsIgnoreCase(CarrierPaymentConstants.FUELSURCHG)).map(CarrierInvoiceDetail::getCarrierChargeUnitRateAmount).reduce(BigDecimal.ZERO, BigDecimal::add);


        if(!validateFuelSurChargeAmountAndInvoiceChargeAmount(fuelInvCharge,mulfuelInvChargeamt,transitInvCharge,multransitInvChargeamt,payment)){
            validToUpdate = false;
            return validToUpdate;
        }
        // New Change - If FuelSurchg is there at Invoice Side without having Transit At Invoice Side then NO Auto Pay Ends

        if(payment.getGroupFlowTypeCode().equals(CarrierPaymentConstants.LTL)){
            log.info("checkInvoiceRulesForAutopay " + payment.getGroupFlowTypeCode() );
            log.info("total group invoicegroupamount  charge amount" + invoicegroupamount);
            getOtherInvDetailList( othrinvoiceDetalList, transitInvCharge, multransitInvChargeamt );

                validToUpdate =processLTLEditTransit(othrinvoiceDetalList,unActionedChargeList,invoicegroupamount,chrgegroupamount);

            if(othrinvoiceDetalList.stream().anyMatch(invoichrge->invoichrge.getCarrierChargeUnitRateAmount().signum()==-1)){
                return false;
            }

            if(validToUpdate) {
                validToUpdate = chkIsValidToUpdate(payment, unActionedChargeList, parameterListings);
            }
            //Auto Pay New WS Changes End
        }else if(IS_GREATER_AUTO_PAY.test(totlchrgeamt,totalinvoicegroupamount)) {
            log.info("checkInvoiceRulesForAutopay " + payment.getGroupFlowTypeCode() );
            boolean editStatus = proceeNONLTLEdits(grpchargeList, invoiceDetail, othrinvoiceDetalList,othrchargeList, chrgegroupamount,invoicegroupamount);
            validToUpdate = returnValidToUpdate( payment, unActionedChargeList, othrchargeList, editStatus );
        }

        logAuotPayActivityFailure( payment, validToUpdate );

        return validToUpdate;
    }

    private void logAuotPayActivityFailure(Payment payment, boolean validToUpdate) {
        if(!validToUpdate){
            log.info( " Logging AutoPay failure -------  Other - UnKnown Error " );
            autoPayService.logAutoPayFailureActivity(payment, CarrierPaymentConstants.ACT_AUTOPAY_FAIL_OTHER);
        }
    }

    public boolean validateChargeAmounstAgainsDb2(Payment payment, CarrierInvoiceHeader ediInvoice){

        ChargeDTO chargeDTO = autoPayService.populateChargeDTO(Optional.of(payment),ediInvoice);
        boolean validToUpdate=  chargeDecisionService.validateChargeAmount(chargeDTO,payment);
        if(!validToUpdate){
            log.info( " Logging AutoPay failure reason when DB2 and SQL Sever Total Charges Amount is not matching or DB2 and SQL autopay not in Sync " );
            autoPayService.logAutoPayFailureActivity(payment, CarrierPaymentConstants.ACT_AUTOPAY_FAIL_TOTCPREXCP);
            paymentHelperService.setPaymentStatusToReroute(payment);
        }
        return  validToUpdate;
    }
    public boolean validateFuelSurChargeAmountAndInvoiceChargeAmount(  Optional<CarrierInvoiceDetail> fuelInvCharge, BigDecimal mulfuelInvChargeamt,  Optional<CarrierInvoiceDetail> transitInvCharge, BigDecimal multransitInvChargeamt,Payment payment){
        if(fuelInvCharge.isPresent() && mulfuelInvChargeamt.compareTo( BigDecimal.ZERO ) > 0){
            log.info( " Invoice Fuel amount is " + mulfuelInvChargeamt );
            if(!transitInvCharge.isPresent() || (multransitInvChargeamt.compareTo( BigDecimal.ZERO )==0)){
                log.info( " Logging AutoPay failure reason when Invoice Fuel is present without Invoice Transit " );
                autoPayService.logAutoPayFailureActivity(payment, CarrierPaymentConstants.ACT_AUTOPAY_FAIL_FUELNOTRAN);
                paymentHelperService.setPaymentStatusToReroute(payment);
                return false;
            }
        }
        return true;
    }

    private boolean returnValidToUpdate(Payment payment, List<Charge> unActionedChargeList, ArrayList<Charge> othrchargeList, boolean editStatus) {
        List<ChargeDTO> chargeDTOS;
        boolean validToUpdate;
        if(editStatus) {
            payment.setCarrierPaymentChargeList(unActionedChargeList);
            chargeDTOS=populateListofChargeDTO(othrchargeList,payment);
            log.info("chargeDTOS in else for FTL :: chargeDTOS: " + chargeDTOS);
            validToUpdate = chargeDecisionService.autopaychargeDecision(payment, chargeDTOS);
        }else{
            validToUpdate=false;
        }
        log.info("chargeDecision in else for FTL :: validToUpdate: " + validToUpdate);
        return validToUpdate;
    }

    private void getOtherInvDetailList(ArrayList<CarrierInvoiceDetail> othrinvoiceDetalList, Optional<CarrierInvoiceDetail> transitInvCharge, BigDecimal multransitInvChargeamt) {
        if(transitInvCharge.isPresent()){
            CarrierInvoiceDetail crrivc = SerializationUtils.clone(transitInvCharge.get());
            crrivc.setCarrierChargeUnitRateAmount(multransitInvChargeamt);
            othrinvoiceDetalList.add(crrivc);
        }
    }

    private boolean chkIsValidToUpdate(Payment payment, List<Charge> unActionedChargeList, List<ParameterListingDTO> parameterListings ) {
        List<ChargeDTO> chargeDTOS;
        boolean validToUpdate;
        //Auto Pay New WS Changes Start

        List<ParameterListingDTO> parameterCustCharge = autoPayService.getRequiredParameterList(parameterListings,CarrierPaymentConstants.SPECIFICATION_APPLY_TO_CUSTOMER );

        List<ParameterListingDTO> paramApplyToCustChargesExclude  = autoPayService.getRequiredParameterList(parameterListings,CarrierPaymentConstants.SPECIFICATION_APPLY_TO_CUSTOMER_CHARGES_EXCLUDE );

        payment.setCarrierPaymentChargeList(unActionedChargeList);
        chargeDTOS=populateListofChargeDTO(unActionedChargeList,payment);
        if( (!ChargePredicateUtil.APGROUP_APPLYTOCUST_CHARGES_AUTOPAY.test(payment.getBillToPartyId(),parameterCustCharge))) {
            setChargeDTO( payment, unActionedChargeList, paramApplyToCustChargesExclude );
        }
        log.info("chargeDTOS in if for LTL :: chargeDTOS: " + chargeDTOS);
        validToUpdate=chargeDecisionService.autopaychargeDecision(payment,chargeDTOS);
        log.info("chargeDecision in if for LTL :: validToUpdate: " + validToUpdate);
        return validToUpdate;
    }

    private void setChargeDTO(Payment payment, List<Charge> unActionedChargeList, List<ParameterListingDTO> paramApplyToCustChargesExclude) {
        log.info("chargeDTOS in if for LTL :: entered Applyto cust begin ::"+ payment.getBillToPartyId());
        unActionedChargeList.forEach(charge-> {
            if( !paramApplyToCustChargesExclude.isEmpty() && !ChargePredicateUtil.APGROUP_APPLYTOCUST_CHARGES_AUTOPAY.test(charge.getChargeCode(),paramApplyToCustChargesExclude) &&
                    Objects.isNull(charge.getExternalChargeBillingID()) ) {
                ChargeDTO chargeDT = new ChargeDTO();

                chargeDT.setChargeDecisionCode(CarrierPaymentConstants.APPROVE);
                chargeDT.setChargeQuantity(charge.getChargeQuantity());
                chargeDT.setExternalChargeID(charge.getExternalChargeID());
                chargeDT.setChargeCode(charge.getChargeCode());
                chargeDT.setCustomerChargeCode(charge.getChargeCode());
                chargeDT.setTotalChargeAmount(charge.getTotalApprovedChargeAmount());
                chargeDT.setLoadNumber(payment.getLoadNumber());
                chargeDT.setDispatchNumber(payment.getDispatchNumber());
                chargeDT.setScacCode(payment.getScacCode());
                chargeDT.setPaymentId(payment.getCarrierPaymentId());
                charge.setChargeApplyToCustomerIndicator('Y');
                chargeDT.setChargeApplyToCustomerIndicator('Y');
            }
        });
    }

    public void getInvoiceDetailList(List<ParameterListingDTO> parameterAutoApprv, List<CarrierInvoiceDetail> invoiceDetalgrpList, List<CarrierInvoiceDetail> othrinvoiceDetalList, CarrierInvoiceDetail invoiceDetal) {
        log.info("get Invoice Detail List value "+ ChargePredicateUtil.CARRERAPGROUP_EDIT_CHARGES.test(invoiceDetal,parameterAutoApprv));
        if(ChargePredicateUtil.CARRERAPGROUP_EDIT_CHARGES.test(invoiceDetal,parameterAutoApprv)){
            invoiceDetalgrpList.add(invoiceDetal);
        }else {
            othrinvoiceDetalList.add(invoiceDetal);
        }
    }

    public void getChargeList(List<ParameterListingDTO> parameterAutoApprv, List<Charge> grpchargeList, List<Charge> othrchargeList, Charge charge) {
        if(ChargePredicateUtil.APGROUP_EDIT_CHARGES.test(charge,parameterAutoApprv)){
            grpchargeList.add(charge);
        }else {
            othrchargeList.add(charge);
        }
    }

    //Auto Pay New WS Changes End

    private List<ChargeDTO> populateListofChargeDTO(List<Charge> charges,Payment payment) {
        ArrayList<ChargeDTO> chargelist=new ArrayList<>();
        charges.forEach(charge-> {
            ChargeDTO chargeDTO = new ChargeDTO();

            chargeDTO.setChargeDecisionCode(CarrierPaymentConstants.APPROVE);
            chargeDTO.setChargeQuantity(charge.getChargeQuantity());
            chargeDTO.setExternalChargeID(charge.getExternalChargeID());
            chargeDTO.setCustomerChargeCode(charge.getChargeCode());
            chargeDTO.setTotalChargeAmount(charge.getTotalApprovedChargeAmount());
            chargeDTO.setLoadNumber(payment.getLoadNumber());
            chargeDTO.setDispatchNumber(payment.getDispatchNumber());
            chargeDTO.setScacCode(payment.getScacCode());
            chargeDTO.setPaymentId(payment.getCarrierPaymentId());
            chargeDTO.setExternalChargeBillingID(charge.getExternalChargeBillingID());
            chargeDTO.setAutoPay(true);
            chargelist.add(chargeDTO);
        });
        return chargelist;
    }

    private void  populateNewOvveride(Charge chgr){
        ChargeOverride newOverridee=new ChargeOverride();
        newOverridee.setChargeId(chgr.getChargeId());
        newOverridee.setCrtS(LocalDateTime.now());
        newOverridee.setCrtPgmC(chgr.getCrtPgmC());
        newOverridee.setCrtUid(chgr.getCrtUid());
        newOverridee.setLstUpdPgmC(CarrierPaymentConstants.SYSTEM);
        newOverridee.setLstUpdS(LocalDateTime.now());
        newOverridee.setLstUpdUid(chgr.getCrtUid());
        newOverridee.setChargeQuantity(chgr.getChargeQuantity());
        chgr.setChargeOverride(newOverridee);
    }

    private Charge editChargeamount(Charge  chrge, BigDecimal transitamount) {
        if(Objects.nonNull(chrge.getChargeOverride())){
            ChargeOverride chrgeovvrde= chargeDecisionService.updateOvverideCharge(chrge);
            chrgeovvrde.setVendorAmount(transitamount);
            chrgeovvrde.setOverrideAmount(transitamount);
            chrge.setChargeOverride(chrgeovvrde);
            chrge.setTotalApprovedChargeAmount(transitamount);
        }else {
            populateNewOvveride(chrge);
            chrge.setTotalApprovedChargeAmount(transitamount);
            chrge.getChargeOverride().setOverrideAmount(transitamount);
            chrge.getChargeOverride().setVendorAmount(transitamount);
        }
        return chrge;

    }

    private Charge editChargeamountLTL(Charge  chrge, BigDecimal transitamount) {

        BigDecimal adjust= chrge.getTotalApprovedChargeAmount().subtract(transitamount)
                .setScale(4, RoundingMode.CEILING);
        if(chrge.getTotalApprovedChargeAmount().compareTo(transitamount)>=0  && (adjust.doubleValue() <=0.01) ){
            log.info(":::: no need to ovverride positive:::"+ chrge.getTotalApprovedChargeAmount());
        }else if(chrge.getTotalApprovedChargeAmount().compareTo(transitamount)<0 && -0.0099 <=adjust.doubleValue()) {
            log.info("::::: no ovverride  negative ::::"+chrge.getTotalApprovedChargeAmount());
        }else
        {
            log.info( "::: OVVERRIDE THE CHARGHE FOR THE AMOUNT  ::: " +chrge.getTotalApprovedChargeAmount() );

            if (Objects.nonNull(chrge.getChargeOverride())) {
                ChargeOverride chrgeOvrde = chargeDecisionService.updateOvverideCharge(chrge);
                chrgeOvrde.setVendorAmount(transitamount);
                chrgeOvrde.setOverrideAmount(transitamount);
                chrge.setChargeOverride(chrgeOvrde);
                chrge.setTotalApprovedChargeAmount(transitamount);
            } else {
                if (chrge.getTotalApprovedChargeAmount().compareTo(transitamount) == 0.00) {
                    log.info( "::: Don't do anything  ::: " );

                } else {
                    populateNewOvveride(chrge);
                    chrge.setTotalApprovedChargeAmount(transitamount);
                    chrge.getChargeOverride().setOverrideAmount(transitamount);
                    chrge.getChargeOverride().setVendorAmount(transitamount);
                }
            }}
        return chrge;

    }

    private void setChargeAmountLTL(BigDecimal invoicegroupamount, Charge chgr) {
        chgr.setTotalApprovedChargeAmount( invoicegroupamount );
        if (null != chgr.getChargeOverride())
            chgr.getChargeOverride().setOverrideAmount( invoicegroupamount );
    }


    private boolean proceeNONLTLEdits(List<Charge> grpchargeList,List<CarrierInvoiceDetail> invoiceDetail,List<CarrierInvoiceDetail> othrinvoiceDetalList,List<Charge> othrchargeList, BigDecimal chrgegroupamount,BigDecimal invoicegroupamount){

        log.info( "------- Inside --------  proceeNONLTLEdits()-----------------" );
        Optional<Charge> transitchrgee=grpchargeList.stream().filter(val3->val3.getChargeCode().trim().equalsIgnoreCase(CarrierPaymentConstants.TRANSIT)).findAny();
        Optional<Charge> fuelchrgee=grpchargeList.stream().filter(val3->val3.getChargeCode().trim().equalsIgnoreCase(CarrierPaymentConstants.FUELSURCHG)).findAny();

        boolean status=false;
        if(transitchrgee.isPresent()){
            log.info( "------- If Transit is Present in proceeNONLTLEdits()-----------------" );
            if(!(transitchrgee.get().getTotalApprovedChargeAmount().compareTo( BigDecimal.ZERO ) == 0 && invoicegroupamount.compareTo( BigDecimal.ZERO ) > 0)) {
                if (!invoiceDetail.isEmpty() && IS_GREATER.test( chrgegroupamount, invoicegroupamount )) {
                    status = chkStatusFTL( chrgegroupamount, invoicegroupamount, transitchrgee, fuelchrgee, status );
                }
                if(status) {
                    editAccessorialsForFTL( othrinvoiceDetalList, othrchargeList, transitchrgee, fuelchrgee );
                }
            }
        }else {
            status = isStatusFTL( othrinvoiceDetalList, othrchargeList, chrgegroupamount, invoicegroupamount, transitchrgee, fuelchrgee );
        }
        log.info( "------- Exiting --------  proceeNONLTLEdits()-----------------" );
        return status;

    }

    private boolean chkStatusFTL(BigDecimal chrgegroupamount, BigDecimal invoicegroupamount, Optional<Charge> transitchrgee, Optional<Charge> fuelchrgee, boolean status) {
        if(transitchrgee.isPresent()){
            BigDecimal diffamount = chrgegroupamount.subtract( invoicegroupamount );
            BigDecimal transitamount = transitchrgee.get().getTotalApprovedChargeAmount().subtract( diffamount );
            if (transitamount.signum() == 1) {
                editChargeamount( transitchrgee.get(), transitamount );
                status = true;
            } else if ((transitamount.signum() == 0 || transitamount.signum() == -1) && fuelchrgee.isPresent() && fuelchrgee.get().getTotalApprovedChargeAmount().compareTo( BigDecimal.ZERO )>0) {
                BigDecimal sumOfTransitAndFuel = transitchrgee.get().getTotalApprovedChargeAmount().add(fuelchrgee.get().getTotalApprovedChargeAmount());
                if(diffamount.compareTo(sumOfTransitAndFuel) <0){
                    BigDecimal finalFuelAmt = fuelchrgee.get().getTotalApprovedChargeAmount().subtract( diffamount );
                    if(finalFuelAmt.compareTo( BigDecimal.ZERO ) >=0){
                        transitchrgee.get().setTotalApprovedChargeAmount(transitchrgee.get().getTotalApprovedChargeAmount());
                        editChargeamount( fuelchrgee.get(), finalFuelAmt );
                        status = true;
                    }else{
                        editChargeamount( transitchrgee.get(), transitchrgee.get().getTotalApprovedChargeAmount().add(finalFuelAmt) );
                        editChargeamount( fuelchrgee.get(), BigDecimal.ZERO );
                        status = true;
                    }
                }
            }
        }
        return status;
    }

    private boolean isStatusFTL(List<CarrierInvoiceDetail> othrinvoiceDetalList, List<Charge> othrchargeList, BigDecimal chrgegroupamount, BigDecimal invoicegroupamount, Optional<Charge> transitchrgee, Optional<Charge> fuelchrgee) {
        boolean status;
        log.info( "------- If Transit is NOT Present in proceeNONLTLEdits()-----------------" );
        if(invoicegroupamount.compareTo( chrgegroupamount ) == 0)
            status = true;
        else
            status = false;
        if(status){
            editAccessorialsForFTL( othrinvoiceDetalList, othrchargeList, transitchrgee, fuelchrgee );
        }
        return status;
    }

    private void editAccessorialsForFTL(List<CarrierInvoiceDetail> othrinvoiceDetalList, List<Charge> othrchargeList, Optional<Charge> transitchrgee, Optional<Charge> fuelchrgee) {
        log.info( "------- Inside editAccessorialsForFTL()-----------------" );
        othrinvoiceDetalList.forEach( invoicee ->
                othrchargeList.forEach( chgr -> {
                    if (invoicee.getChargeCode().trim().equalsIgnoreCase( chgr.getChargeCode().trim())){
                        if (invoicee.getCarrierChargeUnitRateAmount().compareTo( chgr.getTotalApprovedChargeAmount())!=0) {
                            editChargeamount( chgr, invoicee.getCarrierChargeUnitRateAmount() );
                        }
                        else{
                            chgr.setTotalApprovedChargeAmount(invoicee.getCarrierChargeUnitRateAmount());
                        }
                    }
                } )
        );
        if (transitchrgee.isPresent()) {
            othrchargeList.add( transitchrgee.get() );
        }
        if (fuelchrgee.isPresent()) {
            othrchargeList.add( fuelchrgee.get() );
        }
        log.info( "------- Exiting in editAccessorialsForFTL()-----------------" );
    }

    private boolean  processLTLEditTransit(List<CarrierInvoiceDetail> otherInvoiceDetailList,List<Charge> unActionedChargeList,BigDecimal invoicegroupamount, BigDecimal chrgegroupamount) {
        log.info( "------- Inside --------  processLTLEditTransit()-----------------" );
        boolean processLTLEditTransit = false;
        Optional<Charge> transitchrgee = unActionedChargeList.stream().filter( val3 -> val3.getChargeCode().trim().equalsIgnoreCase( CarrierPaymentConstants.TRANSIT ) ).findAny();
        Optional<Charge> fuelchrgee = unActionedChargeList.stream().filter( val3 -> val3.getChargeCode().trim().equalsIgnoreCase( CarrierPaymentConstants.FUELSURCHG ) ).findAny();
        if(transitchrgee.isPresent()){
            if (IS_GREATER.test( chrgegroupamount, invoicegroupamount )) {
                processLTLEditTransit = isProcessLTLEditTransit( invoicegroupamount, chrgegroupamount, processLTLEditTransit, transitchrgee, fuelchrgee );
            } else {
                BigDecimal diffamount = invoicegroupamount.subtract( chrgegroupamount );
                BigDecimal finalTransitAmount = transitchrgee.get().getTotalApprovedChargeAmount().add( diffamount );
                editChargeamountLTL( transitchrgee.get(), finalTransitAmount );
                transitchrgee.get().setTotalApprovedChargeAmount( finalTransitAmount );
                processLTLEditTransit = true;
            }
        }
        if (processLTLEditTransit) {
            processEditsLTL( otherInvoiceDetailList, unActionedChargeList, transitchrgee, fuelchrgee );
        }
        log.info( "------- Exiting --------  processLTLEditTransit()-----------------" );
        return processLTLEditTransit;
    }

    private boolean isProcessLTLEditTransit(BigDecimal invoicegroupamount, BigDecimal chrgegroupamount, boolean processLTLEditTransit, Optional<Charge> transitchrgee, Optional<Charge> fuelchrgee) {
        log.info( "------- Inside --------  processLTLEditTransit() when chrgegroupamount >= invoicegroupamount -----------------" );
        if(transitchrgee.isPresent()){
            BigDecimal diffamount = chrgegroupamount.subtract( invoicegroupamount );
            BigDecimal transitamount = transitchrgee.get().getTotalApprovedChargeAmount().subtract( diffamount );
            if (transitamount.signum() == 1 || transitamount.signum() == 0) {
                editChargeamountLTL( transitchrgee.get(), transitamount );
                processLTLEditTransit = true;
            } else if (transitamount.signum() == -1) {
                BigDecimal removetransitamoun = diffamount.subtract( transitchrgee.get().getTotalApprovedChargeAmount() );
                if(fuelchrgee.isPresent()) {
                    BigDecimal finalamnt1 = fuelchrgee.get().getTotalApprovedChargeAmount().subtract( removetransitamoun );
                    if (finalamnt1.signum() == 1 || finalamnt1.signum() == 0) {
                        editChargeamountLTL( transitchrgee.get(), BigDecimal.ZERO );
                        transitchrgee.get().getChargeOverride().setOverrideAmount( BigDecimal.ZERO );
                        transitchrgee.get().setTotalApprovedChargeAmount( BigDecimal.ZERO );
                        editChargeamountLTL( fuelchrgee.get(), finalamnt1 );
                        processLTLEditTransit = true;
                    }
                }
            }
        }
        return processLTLEditTransit;
    }

    private void processEditsLTL(List<CarrierInvoiceDetail> otherInvoiceDetailList, List<Charge> unActionedChargeList, Optional<Charge> transitchrgee, Optional<Charge> fuelchrgee) {
        otherInvoiceDetailList.forEach( invoicee ->
                unActionedChargeList.forEach( chgr -> {
                    if (null!=chgr && invoicee.getChargeCode().trim().equalsIgnoreCase( chgr.getChargeCode().trim() )) {
                        if (Objects.equals( invoicee.getChargeCode().trim(), CarrierPaymentConstants.TRANSIT )) {
                            setChargeAmountLTL( transitchrgee.get().getTotalApprovedChargeAmount(), chgr );
                        } else if (Objects.equals( invoicee.getChargeCode().trim(), CarrierPaymentConstants.FUELSURCHG )) {
                            setChargeAmountLTL( fuelchrgee.get().getTotalApprovedChargeAmount(), chgr );
                        } else {
                            editChargeamountLTL( chgr, invoicee.getCarrierChargeUnitRateAmount() );
                            setChargeAmountLTL( invoicee.getCarrierChargeUnitRateAmount(), chgr );
                        }

                    }
                } )
        );
    }
}
