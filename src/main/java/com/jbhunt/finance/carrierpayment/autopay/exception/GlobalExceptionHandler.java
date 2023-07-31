package com.jbhunt.finance.carrierpayment.autopay.exception;

import com.jbhunt.business.error.ErrorCode;
import com.jbhunt.finance.carrierpayment.autopay.dto.TxnStatusDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.SQLException;

import static com.jbhunt.finance.carrierpayment.autopay.constants.PaymentChargesConstants.RETRIEVE_PAYMENT_ERR_MSG;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler({ SQLException.class,DataAccessException.class })
    public final ResponseEntity<TxnStatusDTO> handleDataAccessException(Exception ex, WebRequest request){
        log.error(RETRIEVE_PAYMENT_ERR_MSG, ex);
        TxnStatusDTO txnStatusDTO = new TxnStatusDTO();
        txnStatusDTO.setError(ErrorCode.SAFCMN0001.getMessage());
        txnStatusDTO.setSuccess(false);
        return new ResponseEntity(txnStatusDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(IOException.class)
    public void handleIOException(Exception ex, WebRequest request) {
        log.error("Error while calling Elastic"+ request,ex);
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleDefaultException(HttpServletRequest req, Exception ex) {
        log.error("Default Exception: {} URL: {}" + ex.getMessage(), req.getRequestURL(), ex);
        return ResponseEntity.badRequest()
                .header("EXCEPTION", ex.getMessage())
                .body(ex.getMessage());
    }

}
