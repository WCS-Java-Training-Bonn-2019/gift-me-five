package com.gift_me_five.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.gift_me_five.entity.User;

@Controller
public class LoginController {
	
	@GetMapping("/showMyLoginPage")
	public String showMyLoginPage(Model model) {
		model.addAttribute("user", new User());
		return "fancy-login";
	}

		
	// add request mapping for /access-denied
	@GetMapping("/access-denied")
	public String showAccessDenied() {
		return "access-denied";
	}
	


		
}