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

		Wish wish = new Wish();
		if (id != null) {
			Wish myWish = userArtifactsService.getWishIfReceiver(id);
			String imagePath = "wish/" + id + "/picture";

			if (myWish == null) {
				// is it a public wish?
				model.addAttribute("visibility", "public");
				imagePath = "public/wish/" + uniqueUrlReceiver + "/" + id + "/picture";
				myWish = userArtifactsService.getPublicWishForReceiver(id, uniqueUrlReceiver);
				if (myWish == null) {
					return "redirect:/not_authorized";
				}
			}
			model.addAttribute("imagePath", imagePath);
			wish = myWish;
		} else { // no wish id -- try with wishlist id resp. uniqueUrlReceiver for public wish
			Wishlist wishlist = userArtifactsService.getWishlistIfReceiver(wishlistId);
			if (wishlist == null) {
				// is it a public wishlist?
				wishlist = userArtifactsService.getWishlistIfPublic(uniqueUrlReceiver);
                model.addAttribute("visibility", "public");
				if (wishlist == null) {
					return "redirect:/not_authorized";
				}
			}
			// wish will be a new wish on the found wishlist. No picture to display!
			wish.setWishlist(wishlist);
		}

		model.addAttribute("uniqueUrlReceiver", uniqueUrlReceiver);
		model.addAttribute("wish", wish);

		// Populate menu item for own wishlists (titles needed)
		model.addAttribute("myWishlists", userArtifactsService.getAllMyWishlistsAsReceiver());
		// Populate menu item for friends wishlists (titles needed)
		model.addAttribute("friendWishlists", userArtifactsService.getAllMyWishlistsAsGiver());

		return "wishForm";

	}

	@PostMapping({ "/wish", "/public/wish/{uniqueUrlReceiver}" })
	public String saveWish(@PathVariable(required = false) String uniqueUrlReceiver, @ModelAttribute Wish wish) {

		// Find out if there is an allowed wishlist for this wish
		Wishlist privateWishlist = userArtifactsService.getWishlistIfReceiver(wish.getWishlist().getId());
		Wishlist publicWishlist = userArtifactsService.getWishlistIfPublic(uniqueUrlReceiver);

		if (privateWishlist == null && publicWishlist == null) {
			return "redirect:/not_authorized";
		}
		// If it is an existing wish being edited, it has an id
		Long id = wish.getId();
		if (id != null) {
			Wish wishOld = userArtifactsService.getWishIfReceiver(id);
			if (wishOld == null && publicWishlist != null) {
				// Is it a wish from a public wishlist?
				wishOld = userArtifactsService.getPublicWishForReceiver(id, uniqueUrlReceiver);
			}
			if (wishOld == null) {
				return "redirect:/not_authorized";
			}
			// Found an allowed existing wish.
			// keep picture if no picture in posted wish ?
			if (wish.getPicture().length == 0) {
				wish.setPicture(wishOld.getPicture());
			}
		}
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
			wish = userArtifactsService.getWishIfReceiver(id);
		} else {
			wish = userArtifactsService.getPublicWishForReceiver(id, uniqueUrlReceiver);
		}
		if (wish == null) {
			return "redirect:/not_authorized";
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

	@GetMapping({ "/wish/{wishId}/picture", "/public/wish/{uniqueUrl}/{wishId}/picture" })
	public ResponseEntity<byte[]> loadImage(@PathVariable(required = false) String uniqueUrl,
			@PathVariable Long wishId) {

        byte[] picture = userArtifactsService.fetchWishPicture(wishId, uniqueUrl);
		if (picture != null) {
			return ResponseEntity.status(HttpStatus.OK)//
					.contentType(MediaType.IMAGE_JPEG)//
					.body(picture);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND)//
				.build();
	}
	
	@GetMapping({"/wish/delete_picture", "/public/wish/delete_picture/{uniqueUrlReceiver}"})
	public String wishDeletePicture(@RequestParam(required = true) Long id, @PathVariable(required = false) String uniqueUrlReceiver) {
		Wish wish;
		if (uniqueUrlReceiver == null) {
			wish = userArtifactsService.getWishIfReceiver(id);
		} else {
			wish = userArtifactsService.getPublicWishForReceiver(id, uniqueUrlReceiver);
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
				return "redirect:/public/wish/" + uniqueUrlReceiver + "?id=" + id;
			}
		}
	}

}
