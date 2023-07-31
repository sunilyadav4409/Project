package com.jbhunt.finance.carrierpayment.autopay.util;


import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@Slf4j
public class PaymentIntegrationPredicateUtil {

    private PaymentIntegrationPredicateUtil() {

    }

    public static final String N01 = "N01";
    public static final String N21 = "N21";

    public static final BiPredicate<Object, Object> IS_EQUAL = (object1, object2) -> object1.equals(object2);

    public static final UnaryOperator<Integer> STOP_VALUE = value -> Optional.ofNullable(value)
            .map(val -> val * 1000).orElse(0);
    public static final Function<Object, String> STRING_DATA = value -> Optional.ofNullable(value).map(Object::toString)
            .orElseGet(() -> "");
    public static final UnaryOperator<BigDecimal> DECIMAL_DATA = value -> Optional.ofNullable(value)
            .map(val -> val).orElse(BigDecimal.ZERO);


}
