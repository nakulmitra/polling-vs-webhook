package com.devportal.delivery.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class DeliveryController {

	@Autowired
	private RestTemplate restTemplate;
	
	@PostMapping(value = "/place-order-v1")
	public String placeOrderV1() {
		return restTemplate.postForObject("http://localhost:8081/place-order-v1", null, String.class);
	}
	
	@GetMapping(value = "/track-order-v1")
	public String trackStatusV1() {
		while(true) {
			String status = restTemplate.getForObject("http://localhost:8081/order-status-v1", String.class);
			System.out.println("Status is: " + status);
			
			if(status != null && status.equals("Ready")) {
				System.out.println("The order is ready");
				break;
			}
			
			try {
				Thread.sleep(1000);
			}catch(InterruptedException ex) {
				System.out.println("Exception at track-order-v1: " + ex.getMessage());
			}
		}
		
		return "You food is ready";
	}
	
	@PostMapping(value = "/place-order-v2")
	public String placeOrderV2() {
		return restTemplate.postForObject("http://localhost:8081/place-order-v2", null, String.class);
	}
	
	@PostMapping(value = "/order-status-v2")
	public String orderStatusV2(@RequestBody String status) {
		System.out.println("Webhook response: " + status);
		return "Success";
	}

}
