package com.gift_me_five.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gift_me_five.entity.Wishlist;
import com.gift_me_five.repository.ThemeRepository;
import com.gift_me_five.repository.UserRepository;
import com.gift_me_five.repository.WishRepository;
import com.gift_me_five.repository.WishlistRepository;

@Controller
public class WishlistController {

	@Autowired
	private WishlistRepository wishlistRepository;

	@Autowired
	private ThemeRepository themeRepository;

	@Autowired
	private UserRepository receiverRepository;
	
	@Autowired
	private WishRepository wishRepository;
	
	@GetMapping("/wishlistPreview")
	public String displayWishlist(Model model, @RequestParam(required=false) Long id) {
		
		Wishlist wishlist = new Wishlist();
		if (id != null) {
			Optional<Wishlist> optionalWishlist = wishlistRepository.findById(id);
			if (optionalWishlist.isPresent()) {
				wishlist = optionalWishlist.get();
			} else {
				id = null;
			}
		} 
		if (id == null){
			return ("redirect:/wishlist");
		}
		model.addAttribute("currentWishlist", wishlist);
		model.addAttribute("wishes", wishRepository.findByWishlist(wishlist));		
		model.addAttribute("wishlists", wishlistRepository.findAll());
		return ("receiver");
	}
	
	@GetMapping("/wishlist")
	public String upsertWishList(Model model, @RequestParam(required = false) Long id) {

		Wishlist wishlist = new Wishlist();

		// *****************************************************
		// TO DO: Default values must be defined!!!
		// Wishlists of current user must be added to model instead of all wishlists!
		// *****************************************************
		Long receiverId = 1L; // receiver Id default Wert
		Long themeId = 1L; // theme Id default Wert

		if (id != null) {
			Optional<Wishlist> optionalWishlist = wishlistRepository.findById(id);
			if (optionalWishlist.isPresent()) {
				wishlist = optionalWishlist.get();
			} else {
				// Selected wishlist doesn't exist!
				id = null;
			}
		}
		if (id == null) {
			// No wishlist - create new!
			wishlist.setReceiver(receiverRepository.findById(receiverId).get());
			wishlist.setTheme(themeRepository.findById(themeId).get());
		}
		model.addAttribute("wishlists", wishlistRepository.findAll());
		model.addAttribute("wishlist", wishlist);
		model.addAttribute("themes", themeRepository.findAll());
		return "wishlist";
	}

	@PostMapping("/wishlist")
	public String saveWishList(@ModelAttribute Wishlist wishlist) {

		System.out.println("*****Innerhalb von  /wishlist PostMapping ********\n");
		System.out.println("Wishlist Id: " + wishlist.getId());

//		wishlist.setReceiver(recRepository.findById(receiverId).get());
//		wishlist.setTheme(thRepository.findById(themeId).get());

		wishlistRepository.save(wishlist);

		return "redirect:/wishlistPreview?id=" + wishlist.getId();
	}

	@GetMapping("/wishlist/delete")
	public String deleteWishList(@RequestParam Long id) {

		wishlistRepository.deleteById(id);
		
		// *****************************************************
		// TO DO: Receiver must be defined!!!
		// *****************************************************
		
		Wishlist wishlist = wishlistRepository.findFirstByIdGreaterThan(0L);
		String wishlistIdTag = (wishlist != null) ? "?id=" + wishlist.getId(): "" ;
		return "redirect:/wishlistPreview" + wishlistIdTag;
	}

}
