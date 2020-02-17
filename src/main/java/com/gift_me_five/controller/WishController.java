package com.gift_me_five.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gift_me_five.entity.Wish;
import com.gift_me_five.entity.Wishlist;
import com.gift_me_five.repository.WishRepository;
import com.gift_me_five.service.UserArtifactsService;

@Controller
public class WishController {

	@Autowired
	private WishRepository repository;

	@Autowired
	private UserArtifactsService userArtifactsService;

	@GetMapping({ "/wish", "/public/wish/{uniqueUrlReceiver}" })
	public String upsertWish(Model model, @PathVariable(required = false) String uniqueUrlReceiver,
			@RequestParam(required = false) Long wishlistId, @RequestParam(required = false) Long id) {
//		System.out.println("WishlistId" + wishlistId);
		Wish wish = new Wish();
		if (id != null) {
			Wish myWish = userArtifactsService.ownWish(id);

			if (myWish == null) {
				// is it a public wish?
				myWish = userArtifactsService.publicWishReceiver(id, uniqueUrlReceiver);
				if (myWish == null) {
					// TODO:
					// Fehlermeldung - Zugriff nicht erlaubt
					return "redirect:/under_construction";
				}
				// model.addAttribute("visibility", "public");
			}
			wish = myWish;
		} else { // no wish id -- try with wishlist id resp. uniqueUrlReceiver for public wish
			Wishlist wishlist = userArtifactsService.ownWishlist(wishlistId);
			if (wishlist == null) {
				// is it a public wishlist?
				wishlist = userArtifactsService.publicWishlist(uniqueUrlReceiver);
				if (wishlist == null) {
					// TODO:
					// Fehlermeldung - Zugriff nicht erlaubt
					return "redirect:/under_construction";
				}
				// model.addAttribute("visibility", "public");
			}
			// wish will be a new wish on the found wishlist
			wish.setWishlist(wishlist);
		}

		model.addAttribute("uniqueUrlReceiver", uniqueUrlReceiver);
		model.addAttribute("wish", wish);

		// Populate menu item for own wishlists (titles needed)
		model.addAttribute("myWishlists", userArtifactsService.allOwnWishlists());
		// Populate menu item for friends wishlists (titles needed)
		model.addAttribute("friendWishlists", userArtifactsService.allFriendWishlists());

		return "wishForm";

	}

	@PostMapping({ "/wish", "/public/wish/{uniqueUrlReceiver}" })
	public String saveWish(@PathVariable(required = false) String uniqueUrlReceiver, @ModelAttribute Wish wish) {

		// Find out if there is an allowed wishlist for this wish
		Wishlist privateWishlist = userArtifactsService.ownWishlist(wish.getWishlist().getId());
		Wishlist publicWishlist = userArtifactsService.publicWishlist(uniqueUrlReceiver);

		if (privateWishlist == null && publicWishlist == null) {
			// TODO:
			// Fehlermeldung - Zugriff nicht erlaubt
			return "redirect:/under_construction";
		}
		// If it is an existing wish being edited, it has an id
		Long id = wish.getId();
		if (id != null) {
			Wish wishOld = userArtifactsService.ownWish(id);
			if (wishOld == null && publicWishlist != null) {
				// Is it a wish from a public wishlist?
				wishOld = userArtifactsService.publicWishReceiver(id, uniqueUrlReceiver);
			}
			if (wishOld == null) {
				// TODO:
				// Fehlermeldung - Zugriff nicht erlaubt
				return "redirect:/under_construction";
			}
			// Found an allowed existing wish.
			// keep picture if no picture in posted wish ?
			if (wish.getPicture().length == 0) {
				wish.setPicture(wishOld.getPicture());
			}
		}
//		GiftMeFive.debugOut(wish, 30);
		repository.save(wish);
		if (uniqueUrlReceiver == null) {
			return "redirect:/receiver?id=" + wish.getWishlist().getId();
		} else {
			return "redirect:/public/receiver/" + uniqueUrlReceiver;
		}
	}

	@GetMapping({ "/wish/delete", "/public/wish/{uniqueUrlReceiver}/delete" })
	public String deleteWish(@PathVariable(required = false) String uniqueUrlReceiver, @RequestParam Long id) {

		Wish wish;
		if (uniqueUrlReceiver == null) {
			wish = userArtifactsService.ownWish(id);
		} else {
			wish = userArtifactsService.publicWishReceiver(id, uniqueUrlReceiver);
		}
		if (wish == null) {
			// TODO:
			// Fehlermeldung - Zugriff nicht erlaubt
			return "redirect:/under_construction";
		} else {
			Long wishlistId = wish.getWishlist().getId();
			repository.deleteById(id);
			if (uniqueUrlReceiver == null) {
				return "redirect:/receiver?id=" + wishlistId;
			} else {
				return "redirect:/public/receiver/" + uniqueUrlReceiver;
			}
		}
	}

	// sample /wish/25/picture

	@GetMapping({ "/wish/{wishId}/picture", "/public/wish/{uniqueUrl}/{wishId}/picture" })
	public ResponseEntity<byte[]> loadImage(@PathVariable(required = false) String uniqueUrl,
			@PathVariable Long wishId) {

		Wish wish = (uniqueUrl == null ? userArtifactsService.ownWish(wishId)
				: userArtifactsService.publicWishGiver(wishId, uniqueUrl));

		if (wish != null && wish.getPicture() != null) {
			return ResponseEntity.status(HttpStatus.OK)//
					.contentType(MediaType.IMAGE_JPEG)//
					.body(wish.getPicture());
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND)//
				.build();
	}
	
	@GetMapping({"/wish/delete_picture", "/public/wish/delete_picture/{uniqueUrlReceiver}"})
	public String wishDeletePicture(@RequestParam(required = true) Long id, @PathVariable(required = false) String uniqueUrlReceiver) {
		Wish wish;
		if (uniqueUrlReceiver == null) {
			wish = userArtifactsService.ownWish(id);
		} else {
			wish = userArtifactsService.publicWishReceiver(id, uniqueUrlReceiver);
		}
		if (wish == null) {
			// TODO:
			// Fehlermeldung - Zugriff nicht erlaubt
			return "redirect:/under_construction";
		} else {
			wish.setPicture(null);
			repository.save(wish);
			if (uniqueUrlReceiver == null) {
				return "redirect:/wish?id=" + id;
			} else {
				return "redirect:/public/wish/" + uniqueUrlReceiver;
			}
		}
	}

}
