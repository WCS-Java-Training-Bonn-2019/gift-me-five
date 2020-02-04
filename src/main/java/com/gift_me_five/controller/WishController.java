package com.gift_me_five.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gift_me_five.entity.Wish;
import com.gift_me_five.entity.Wishlist;
import com.gift_me_five.repository.WishRepository;
import com.gift_me_five.repository.WishlistRepository;

@Controller
public class WishController {

	@Autowired
	private WishRepository repository;

	@Autowired
	private WishlistRepository wishlistRepository;

	@GetMapping("/wish")
	public String upsertWish(Model model, @RequestParam(required = false) Long wishlistId,
			@RequestParam(required = false) Long id) {
//		System.out.println("WishlistId" + wishlistId);
		Wish wish = new Wish();
		if (id != null) {
			Optional<Wish> optionalWish = repository.findById(id);
			if (optionalWish.isPresent()) {
				wish = optionalWish.get();
			}
		}

		if (id == null) {
			Wishlist wishlist = wishlistRepository.findById(wishlistId).get();
			wish.setWishlist(wishlist);
		}

		model.addAttribute("wish", wish);
		// **********************************************************************************
		// TO DO: Retrieve wishlists only for current user, not all wishlists!
		// Wishlists required to build the navigation menu properly for this user.
		// **********************************************************************************
		model.addAttribute("wishlists", wishlistRepository.findAll());

		return "wishForm";
	}

	@PostMapping("/wish")
	public String saveWish(@ModelAttribute Wish wish) {

		Wish wishOld = new Wish();

		if (wish.getId() != null) {
			Optional<Wish> optionalWish = repository.findById(wish.getId());
			if (optionalWish.isPresent()) {
				wishOld = optionalWish.get();
				Wishlist wishlist = wishOld.getWishlist();
				wish.setWishlist(wishlist);

			}
		}

		repository.save(wish);

		// return "redirect:/wish?id=" + wish.getId();
		return "redirect:/receiver?id=" + wish.getWishlist().getId();
	}

	@GetMapping("/wish/delete")
	public String deleteWish(@RequestParam Long id) {

		Wish wish = repository.findById(id).get();
		Long wishlistId = wish.getWishlist().getId();
		repository.deleteById(id);

		return "redirect:/receiver?id=" + wishlistId;
	}

}
