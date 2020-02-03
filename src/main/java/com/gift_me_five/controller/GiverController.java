package com.gift_me_five.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.gift_me_five.entity.Wish;
import com.gift_me_five.entity.Wishlist;
import com.gift_me_five.repository.UserRepository;
import com.gift_me_five.repository.WishRepository;
import com.gift_me_five.repository.WishlistRepository;

@Controller
public class GiverController {

	@Autowired
	private WishRepository repository;

	@Autowired
	private WishlistRepository wishlistRepository;
	
	@Autowired
	private UserRepository userRepository;

	@GetMapping("/giver")
	public String getAll(Model model) {
//		model.addAttribute("wishes", repository.findAll());
//		model.addAttribute("wishes", repository.findByIdLessThan(7L));
//		model.addAttribute("wishes", repository.findById(2L));
		Wishlist wishlist = wishlistRepository.findById(1L).get();
		//****************************************************************************
		// TO DO: (when user handling is enabled)
		// - add all OWN wishlists as attribute myWishlists (only titles would be required)
		// - add all wishlists I'm invited to... model attribute still tbd  (only titles would be required)
		//   (requires action also in other controllers and in header.html)
		// - add current wishlist Id (or better add full wishlist instead of wishes?)
		//*****************************************************************************
		model.addAttribute("myWishlists", wishlistRepository.findAll());
		model.addAttribute("wishlist", wishlist);
		model.addAttribute("wishes", repository.findByWishlist(wishlist));
		return "giver";
	}

	@PostMapping("/giver")
	public String updateWish(@ModelAttribute(value = "wishId") Long wishId,
			@ModelAttribute(value = "giverId") Long giverId) {
		//System.out.println("Wish ID = " + wishId);
		Wish wish = repository.findById(wishId).get();
		if (giverId == 0) {
			wish.setGiver(null);
		} else {
			wish.setGiver(userRepository.findById(giverId).get());
		}
		repository.save(wish);
		return "redirect:/giver";
	}
}
