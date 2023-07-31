//package com.jbhunt.finance.carrierpayment.autopay.dao;
//
//import com.jbhunt.finance.carrierpayment.autopay.dto.PaymentEnrichmentDTO;
//import com.jbhunt.finance.carrierpayment.autopay.mocks.PaymentEnrichmentDTOMock;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.MockitoJUnitRunner;
//import org.springframework.jdbc.core.BeanPropertyRowMapper;
//import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Properties;
//
//import static junit.framework.TestCase.assertNotNull;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyMap;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.when;
//@RunWith(MockitoJUnitRunner.class)
//public class OrderDAOTest {
//	@InjectMocks
//	OrderDAO orderDAO;
//	@Mock
//	NamedParameterJdbcTemplate namedParameterJdbcTemplate;
//	@Mock
//	private Properties sqlProperties;

//	@Test
//	public void testRetrieveOrderIdFromLoadNumber()  {
//		String sql= "test SQL";
//		ResultSetWrappingSqlRowSet resultSet = Mockito.mock(ResultSetWrappingSqlRowSet.class);
//		Mockito.when(resultSet.next()).thenReturn(true,false);
//		Mockito.when(resultSet.getInt(any())).thenReturn(1234);
//		when(sqlProperties.getProperty("getOrderIdByLoadMain")).thenReturn(sql);
//		when(namedParameterJdbcTemplate.queryForRowSet(Mockito.any(), any(HashMap.class))).thenReturn(resultSet);
//		Integer orderId =orderDAO.retrieveOrderIdFromLoadNumber("1234");
//		Mockito.verify(namedParameterJdbcTemplate, times(1)).queryForRowSet(any(), any(HashMap.class));
//		assertNotNull(orderId);
//		assertThat(orderId, is(1234));
//	}

//	@Test
//	public void testRetrieveOrderIdFromLoadNumberWH()  {
//		String sql= "test SQL";
//		ResultSetWrappingSqlRowSet resultSet = Mockito.mock(ResultSetWrappingSqlRowSet.class);
//		Mockito.when(resultSet.next()).thenReturn(false);
//		when(sqlProperties.getProperty("getOrderIdByLoadMain")).thenReturn(sql);
//		when(namedParameterJdbcTemplate.queryForRowSet(Mockito.any(), any(HashMap.class))).thenReturn(resultSet);
//		Integer orderId =orderDAO.retrieveOrderIdFromLoadNumber("1234");
//		Mockito.verify(namedParameterJdbcTemplate, times(2)).queryForRowSet(any(), any(HashMap.class));
//		assertNull(orderId);
//	}
//	@Test
//	public void testRetrieveLoadDetailsFromOrderId()  {
//		String sql= "test SQL";
//		List<PaymentEnrichmentDTO> paymentEnrichmentDTOList = new ArrayList<>();
//		paymentEnrichmentDTOList.add(PaymentEnrichmentDTOMock.paymentEnrichmentMock());
//		when(sqlProperties.getProperty("getLoadDetailsByOrderIdMain")).thenReturn(sql);
//		when(namedParameterJdbcTemplate.query(any(),anyMap(),any(BeanPropertyRowMapper.class))).thenReturn(paymentEnrichmentDTOList);
//		PaymentEnrichmentDTO paymentEnrichmentDTO =orderDAO.retrieveLoadDetailsFromOrderId(1234,false);
//		Mockito.verify(namedParameterJdbcTemplate, times(1)).query(any(),anyMap(),any(BeanPropertyRowMapper.class));
//		assertNotNull(paymentEnrichmentDTO);
//	}
//	@Test
//	public void testRetrieveLoadDetailsFromOrderIdWH()  {
//		String sql= "test SQL";
//		List<PaymentEnrichmentDTO> paymentEnrichmentDTOList = new ArrayList<>();
//		paymentEnrichmentDTOList.add(PaymentEnrichmentDTOMock.paymentEnrichmentMock());
//		when(sqlProperties.getProperty("getLoadDetailsByOrderIdWH")).thenReturn(sql);
//		when(namedParameterJdbcTemplate.query(any(),anyMap(),any(BeanPropertyRowMapper.class))).thenReturn(paymentEnrichmentDTOList);
//		PaymentEnrichmentDTO paymentEnrichmentDTO =orderDAO.retrieveLoadDetailsFromOrderId(1234,true);
//		Mockito.verify(namedParameterJdbcTemplate, times(1)).query(any(),anyMap(),any(BeanPropertyRowMapper.class));
//		assertNotNull(paymentEnrichmentDTO);
//	}
//}
