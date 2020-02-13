package com.gift_me_five.controller;

import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gift_me_five.GiftMeFive;
import com.gift_me_five.entity.Wish;
import com.gift_me_five.entity.Wishlist;
import com.gift_me_five.repository.UserRepository;
import com.gift_me_five.repository.WishRepository;
import com.gift_me_five.repository.WishlistRepository;
import com.gift_me_five.service.UserArtifactsService;

@Controller
public class WishController {

	@Autowired
	private WishRepository repository;

	@Autowired
	private WishlistRepository wishlistRepository;

	@Autowired
	private UserArtifactsService userArtifactsService;

	@GetMapping("/wish")
	public String upsertWish(Model model, Principal principal, Authentication authentication,
			@RequestParam(required = false) Long wishlistId, @RequestParam(required = false) Long id) {
//		System.out.println("WishlistId" + wishlistId);
		Wish wish = new Wish();
		if (id != null) {
			Wish myWish = userArtifactsService.ownWish(id);
//		if (myWish != null) {
//			Optional<Wish> optionalWish = repository.findById(id);
//			if (optionalWish.isPresent()) {
//				wish = optionalWish.get();
//			}
//		}
			if (myWish == null) {
				// TO DO:
				// Fehlermeldung - Zugriff nicht erlaubt
				return "redirect:/under_construction";
			} else {
				wish = myWish;
			}
		} else {
			Wishlist wishlist = userArtifactsService.ownWishlist(wishlistId);
			if (wishlist == null) {
				// TO DO:
				// Fehlermeldung - Zugriff nicht erlaubt
				return "redirect:/under_construction";
			} else {
				wish.setWishlist(wishlist);
			}
		}

		model.addAttribute("wish", wish);
		// **********************************************************************************
		// TO DO: Retrieve wishlists only for current user, not all wishlists!
		// Wishlists required to build the navigation menu properly for this user.
		// **********************************************************************************
		// Populate menu item for own wishlists (titles needed)
		model.addAttribute("myWishlists", userArtifactsService.allOwnWishlists());
		// Populate menu item for friends wishlists (titles needed)
		model.addAttribute("friendWishlists", userArtifactsService.allFriendWishlists());

		return "wishForm";
	}

	@PostMapping("/wish")
	public String saveWish(@ModelAttribute Wish wish) {

		Wish wishOld = new Wish();

		if (wish.getId() != null) {
			Optional<Wish> optionalWish = repository.findById(wish.getId());
			if (optionalWish.isPresent()) {
				wishOld = optionalWish.get();
				// is picture in wish ?
				if (wish.getPicture().length == 0) {
					wish.setPicture(wishOld.getPicture());
				}
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

	// sample /wish/25/picture
	@GetMapping("/wish/{wishId}/picture")
	public ResponseEntity<byte[]> loadImage(@PathVariable Long wishId) {

		Optional<Wish> optionalWish = repository.findById(wishId);
		if (optionalWish.isPresent() && optionalWish.get().getPicture() != null) {
			Wish wish = optionalWish.get();
			return ResponseEntity.status(HttpStatus.OK)//
					.contentType(MediaType.IMAGE_JPEG)//
					.body(wish.getPicture());
		}

		return ResponseEntity.status(HttpStatus.NOT_FOUND)//
				.build();
	}

}
