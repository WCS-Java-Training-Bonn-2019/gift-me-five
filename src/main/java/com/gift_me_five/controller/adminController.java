package com.gift_me_five.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.gift_me_five.repository.UserRepository;
import com.gift_me_five.repository.WishRepository;
import com.gift_me_five.repository.WishlistRepository;

@Controller
public class adminController {

	@Autowired
	private WishRepository wishRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private WishlistRepository wishlistRepository;
	
	@GetMapping("/admin/user")
	public String getAll(Model model) {
		model.addAttribute("users", userRepository.findAll());
		return "admin/get_all_User";
	}
	
	@GetMapping("/admin/wish")
	public String getWish() {
		return "redirect:/under_construction";
	}
	
	@GetMapping("/admin/wishlist")
	public String getWishlist() {
		return "redirect:/under_construction";
	}
}
