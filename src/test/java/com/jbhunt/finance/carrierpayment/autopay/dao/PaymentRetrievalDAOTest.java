//package com.jbhunt.finance.carrierpayment.autopay.dao;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.MockitoJUnitRunner;
//import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
//import org.springframework.jdbc.support.rowset.ResultSetWrappingSqlRowSet;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Properties;
//
//import static junit.framework.TestCase.assertNotNull;
//import static org.hamcrest.Matchers.is;
//import static org.junit.Assert.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyInt;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.when;
//
//@RunWith(MockitoJUnitRunner.class)
//public class PaymentRetrievalDAOTest {
//    @InjectMocks
//    PaymentRetrievalDAO paymentRetrievalDAO;
//
//    @Mock
//    NamedParameterJdbcTemplate namedParameterJdbcTemplate;
//    @Mock
//    private Properties sqlProperties;
//
//    @Test
//    public void tesRetrieveReferenceNumbers()  {
//        Map<String, String> referenceNumbers = new HashMap<>();
//        String sql= "test SQL";
//        ResultSetWrappingSqlRowSet resultSet = Mockito.mock(ResultSetWrappingSqlRowSet.class);
//        Mockito.when(resultSet.next()).thenReturn(true,false);
//        Mockito.when(resultSet.getString(anyInt())).thenReturn("test","testing ");
//        when(sqlProperties.getProperty("getReferenceNumbersByOrderIdMain")).thenReturn(sql);
//        when(namedParameterJdbcTemplate.queryForRowSet(Mockito.any(), any(HashMap.class))).thenReturn(resultSet);
//        referenceNumbers =paymentRetrievalDAO.retrieveReferenceNumbers(1234, false);
//        Mockito.verify(namedParameterJdbcTemplate, times(1)).queryForRowSet(any(), any(HashMap.class));
//        assertNotNull(referenceNumbers);
//        assertThat(referenceNumbers.get("test"), is("testing"));
//
//    }
//    @Test
//    public void tesRetrieveReferenceNumbersWH()  {
//        Map<String, String> referenceNumbers = new HashMap<>();
//        String sql= "test SQL";
//        ResultSetWrappingSqlRowSet resultSet = Mockito.mock(ResultSetWrappingSqlRowSet.class);
//        Mockito.when(resultSet.next()).thenReturn(true,false);
//        Mockito.when(resultSet.getString(anyInt())).thenReturn("test ","testing ");
//        when(sqlProperties.getProperty("getReferenceNumbersByOrderIdWH")).thenReturn(sql);
//        when(namedParameterJdbcTemplate.queryForRowSet(Mockito.any(), any(HashMap.class))).thenReturn(resultSet);
//        referenceNumbers =paymentRetrievalDAO.retrieveReferenceNumbers(1234, true);
//        Mockito.verify(namedParameterJdbcTemplate, times(1)).queryForRowSet(any(), any(HashMap.class));
//        assertNotNull(referenceNumbers);
//        assertThat(referenceNumbers.get("test"), is("testing"));
//
//    }
//
//}
