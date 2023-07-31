package com.jbhunt.finance.carrierpayment.autopay.util.payment;

import com.jbhunt.finance.carrierpayment.autopay.dto.TxnStatusDTO;
import com.jbhunt.finance.carrierpayment.autopay.entity.Charge;
import com.jbhunt.finance.carrierpayment.autopay.entity.Payment;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class ChargeCommonUtil {

    private ChargeCommonUtil() {

    }

    public static void txnStatusErrorList(TxnStatusDTO txnStatusDTO, Map<String, Boolean> flagMap) {

        List<String> errorList = flagMap.entrySet().stream().filter(map -> Boolean.FALSE.equals(map.getValue()))
                .map(Entry::getKey).collect(Collectors.toList());

        txnStatusDTO.getErrorList().addAll(errorList);
    }

    public static void logStatusDTO(TxnStatusDTO txnStatusDTO) {
        log.info("txnStatusDTO: " + txnStatusDTO);
        log.info("txnStatusDTO.getErrorList.size: " + txnStatusDTO.getErrorList().size());
        txnStatusDTO.getErrorList().forEach(error -> log.info("ERROR STRING ::" + error));
        log.info("txnStatusDTO.getWarning: " + txnStatusDTO.getWarning());
    }

    // TOTAL INVOICE APPROVED AMOUNT
    public static void setTotalApprovedInvoiceAmt(List<Charge> allActiveCharges, Payment payment) {
        Optional.ofNullable(allActiveCharges).filter(list -> !list.isEmpty()).ifPresent(
                chargeList -> payment.setTotalApprovedInvoiceAmt(CalcAmountUtil.calculateApprovedAmount(chargeList)));
    }

    public static TxnStatusDTO stalePayment(TxnStatusDTO notStale) {
        TxnStatusDTO txnStatusDTO;
        txnStatusDTO = notStale;
        log.info("CHARGE:: PAYMENT IS IN STALE STATE!");
        return txnStatusDTO;
    }
    public static boolean isNullOrEmpty(Collection... collections) {
        for (Collection collection : collections) {
            if (null == collection || collection.isEmpty())
                return true;
        }
        return false;
    }

}
