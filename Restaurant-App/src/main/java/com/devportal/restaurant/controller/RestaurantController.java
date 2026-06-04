package com.devportal.restaurant.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class RestaurantController {

	@Autowired
	private RestTemplate restTemplate;
	
	private String orderStatus = "Processing";
	
	@PostMapping(value = "/place-order-v1")
	public String createOrderV1() {
		orderStatus = "Processing";
		
		new Thread(() -> {
			try {
				Thread.sleep(15000);
			}catch(InterruptedException ex) {
				System.err.println("Exception at create order v1 endpoint: " + ex.getMessage());
			}
			
			orderStatus = "Ready";
		}).start();
		
		
		return "We are preparing your order...";
	}
	
	@GetMapping(value = "/order-status-v1")
	public String getOrderStatusV1() {
		return orderStatus;
	}
	
	@PostMapping(value = "/place-order-v2")
	public String createOrderV2() {
		orderStatus = "Processing";
		System.out.println("Preparing your food...");
		
		new Thread(() -> {
			try {
				Thread.sleep(15000);
			}catch(InterruptedException ex) {
				System.err.println("Exception at create order v1 endpoint: " + ex.getMessage());
			}
			
			orderStatus = "Ready";
			System.out.println("Your food is ready...");
			restTemplate.postForObject("http://localhost:8080/order-status-v2", orderStatus, String.class);
		}).start();
		
		
		return "We are preparing your order...";
	}

}
