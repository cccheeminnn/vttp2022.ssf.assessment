package vttp2022.nus.iss.edu.sg.assessment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.HttpClientErrorException;

import vttp2022.nus.iss.edu.sg.assessment.Model.Quotation;
import vttp2022.nus.iss.edu.sg.assessment.Service.QuotationService;

@SpringBootTest
class AssessmentApplicationTests {

	@Autowired
	private QuotationService quotationSvc;

	@Test
	void contextLoads() {
	}

	@Test
	public void getQuotationsTest() 
	{
		List<String> testList1 = new ArrayList<>();
		testList1.add("durian");
		testList1.add("plum");
		testList1.add("pear");

		Quotation quotation = new Quotation();
		Optional<Quotation> quotationOptional = Optional.of(quotation);
		
		try {
			quotationOptional = quotationSvc.getQuotations(testList1);
		} catch (HttpClientErrorException ex) {
			Assertions.assertTrue(ex.getRawStatusCode() >= 400);
		}
	}
}