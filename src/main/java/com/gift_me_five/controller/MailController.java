package com.gift_me_five.controller;

import java.security.Principal;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gift_me_five.entity.Wishlist;
import com.gift_me_five.repository.WishlistRepository;
import com.gift_me_five.service.UserArtifactsService;

@Controller
public class MailController {

	@Autowired
	UserArtifactsService userArtifactsService;
	
	@Autowired
	WishlistRepository wishlistRepository;

	@GetMapping("/invite")
	public String showInvite(Model model, Principal principal, @RequestParam(required = false) Long wishlistId) {

		model.addAttribute("myWishlists", userArtifactsService.allOwnWishlists());
		model.addAttribute("friendWishlists", userArtifactsService.allFriendWishlists());
		model.addAttribute("wishlist", wishlistRepository.findById(wishlistId));
		return "invite";
	}
	
	@PostMapping("/send_invite")
	public String sendInvite(Model model, @Valid Wishlist wishlist) {
//		GiftMeFive.debugOut(wishlist.toString());
		

		return "redirect:/invite?wishlistId=" + wishlist.getId();
	}
}