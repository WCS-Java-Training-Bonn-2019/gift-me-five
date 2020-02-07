package com.gift_me_five.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gift_me_five.GiftMeFive;
import com.gift_me_five.entity.User;
import com.gift_me_five.service.UserArtifactsService;

@Controller
public class LoginController {
	
	@Autowired
	UserArtifactsService userArtifactsService;
	
	@GetMapping("/")
	public String showStartPage(Model model, @RequestParam (required = false) Long loginFailure) {
		
		model.addAttribute("myWishlists", userArtifactsService.allOwnWishlists());
		model.addAttribute("friendWishlists", userArtifactsService.allFriendWishlists());
		model.addAttribute("loginFailure" , loginFailure);
		
		//GiftMeFive.debugOut(principal.getName());
		
		return "index";
	}
	
				
	// add request mapping for /access-denied
	@GetMapping("/access-denied")
	public String showAccessDenied() {
		return "access-denied";
	}
	


		
}