package com.jbhunt.finance.carrierpayment.autopay.util.payment;

import java.math.BigDecimal;
import java.util.function.BiPredicate;

public class CarrierPaymentPredicateUtil {

    private CarrierPaymentPredicateUtil() {

    }

    public static final BiPredicate<BigDecimal, BigDecimal> IS_GREATER = (value,
                                                                          lowerLimt) -> value.compareTo(lowerLimt) >= 0;
    public static final BiPredicate<BigDecimal, BigDecimal> IS_LESSER = (value,
                                                                         upperLimt) -> value.compareTo(upperLimt) <= 0;

    public static final BiPredicate<Boolean, Boolean> AND = (val1, val2) -> val1 && val2;

    public static final BiPredicate<BigDecimal, BigDecimal> IS_GREATER_AUTO_PAY = (value,lowerLimit) -> value.compareTo(lowerLimit) > 0;

}
