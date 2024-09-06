package com.bookgo.cart.controller;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

import lombok.extern.slf4j.Slf4j;



@Slf4j
@Controller
@RequestMapping("/verifyiamport")
public class IamportController {
	
	private final IamportClient iamportClient;
	private static final String API_KEY = "0078013824064371";
	private static final String API_SECRET = "t9j1gP8DrrGaw7ZZ1RIfHyd6fnK6Wnz4pnrXbV1IZKF2PF8q0Ufq9C5LQ2F3Z5TBfFn1KTtEPVWC0A00";
	
	public IamportController() {
		this.iamportClient = new IamportClient(API_KEY, API_SECRET);
	}

	/**
	 * 아임포트(Iamport) 클라이언트-서버간 결제 검증
	 * @apiNote API Keys 토대로 IamportClient 생성
	 * @throws IamportResponseException, IOException
	 * @param imp_uid
	 */
	@PostMapping("/{imp_uid}")
	@ResponseBody
	public IamportResponse<Payment> paymentByImpUid(@PathVariable(value="imp_uid") String imp_uid) throws IamportResponseException, IOException {
		log.info("paymentByImpUid");
		return iamportClient.paymentByImpUid(imp_uid);
	}
}
