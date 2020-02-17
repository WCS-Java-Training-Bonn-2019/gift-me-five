package com.gift_me_five.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UnderConstructionController {
	
	@GetMapping("/under_construction")
	public String showConstructionSite() {
		return "under_construction/index";
	}

}
