package com.gift_me_five.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.gift_me_five.entity.User;
import com.gift_me_five.service.UserArtifactsService;

@Controller
public class LoginController {
	
	@Autowired
	UserArtifactsService userArtifactsService;
	
	@GetMapping("/")
	public String showStartPage(Model model) {
		
		model.addAttribute("myWishlists", userArtifactsService.allOwnWishlists());
		model.addAttribute("friendWishlists", userArtifactsService.allFriendWishlists());
		
		return "index";
	}
	
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