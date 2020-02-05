package com.gift_me_five.controller;

import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gift_me_five.entity.Wish;
import com.gift_me_five.entity.Wishlist;
import com.gift_me_five.repository.ThemeRepository;
import com.gift_me_five.repository.UserRepository;
import com.gift_me_five.repository.WishRepository;
import com.gift_me_five.repository.WishlistRepository;
import com.gift_me_five.service.UserArtifactsService;

@Controller
public class WishlistController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private WishlistRepository wishlistRepository;

	@Autowired
	private ThemeRepository themeRepository;

	@Autowired
	private UserRepository receiverRepository;

	@Autowired
	private WishRepository wishRepository;

	@Autowired
	private UserArtifactsService userArtifactsService;

	@GetMapping("/giver")
	public String giverWishlistView(Model model, Principal principal, Authentication authentication,
			@RequestParam(required = false) Long id) {

		Wishlist wishlist = userArtifactsService.friendWishlist(id);
		System.out.println("*".repeat(40));
		System.out.println("*".repeat(40));
		System.out.println("Wishlist ID: " + wishlist.getId());
		System.out.println("*".repeat(40));
		System.out.println("*".repeat(40));		
		if (wishlist != null) {
			model.addAttribute("myUserId", userArtifactsService.getCurrentUser().getId());
			model.addAttribute("thisWishlistId", wishlist.getId());
			model.addAttribute("myWishlists", userArtifactsService.allOwnWishlists());
			model.addAttribute("friendWishlists", userArtifactsService.allFriendWishlists());
			model.addAttribute("wishlist", wishlist);
			model.addAttribute("wishes", wishRepository.findByWishlist(wishlist));
			System.out.println("*".repeat(40));
			System.out.println("*".repeat(40));
			System.out.println("User ID: " + userArtifactsService.getCurrentUser().getId());
			for (int i=0; i<userArtifactsService.allFriendWishlists().size(); i++) {
				System.out.print("Friend's Wishlist IDs:  ");
				System.out.print(userArtifactsService.allFriendWishlists().get(i).getId() + ",  ");
				System.out.println();
			}
			System.out.println("*".repeat(40));
			System.out.println("*".repeat(40));	
			return "giver";
		}
		// Hier sollte besser eine Meldung auftauchen, dass keine Wishlist angezeigt
		// werden kann.
		return "redirect:/under_construction";

	}

	@PostMapping("/giver")
	public String updateWish(@ModelAttribute(value = "wishId") Long wishId) {
		// System.out.println("Wish ID = " + wishId);
		Wish wish = wishRepository.findById(wishId).get();
		Wishlist wishlist = userArtifactsService.friendWishlist(wish.getWishlist().getId());
		if (wishlist != null) { // Current User admitted for this wish's wishlist
			if (wish.getGiver() == null) {
				wish.setGiver(userArtifactsService.getCurrentUser());
			} else if (userArtifactsService.getCurrentUser() == wish.getGiver()) {
				wish.setGiver(null);
			}
			wishRepository.save(wish);
			return "redirect:/giver?id=" + wishlist.getId();
		}
		// Sollte nur nach Manipulation (e.g. curl) erreicht werden:
		// User versucht, eine wishlist zu verändern, für die er nicht als giver registriert ist
		return "redirect:/under_construction";
	}

	@GetMapping("/receiver")
	public String displayWishlist(Model model, @RequestParam(required = false) Long id) {

		Wishlist wishlist = new Wishlist();
		if (id != null) {
			Optional<Wishlist> optionalWishlist = wishlistRepository.findById(id);
			if (optionalWishlist.isPresent()) {
				wishlist = optionalWishlist.get();
			} else {
				id = null;
			}
		}
		if (id == null) {
			return ("redirect:/wishlist");
		}
		model.addAttribute("currentWishlist", wishlist);
		model.addAttribute("wishes", wishRepository.findByWishlist(wishlist));
		model.addAttribute("wishlists", wishlistRepository.findAll());
		return ("receiver");
	}

	@GetMapping({ "/wishlist", "/newwishlist" })
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

		return "redirect:/receiver?id=" + wishlist.getId();
	}

	@GetMapping("/wishlist/delete")
	public String deleteWishList(@RequestParam Long id) {

		wishlistRepository.deleteById(id);

		// *****************************************************
		// TO DO: Receiver must be defined!!!
		// *****************************************************

		Wishlist wishlist = wishlistRepository.findFirstByIdGreaterThan(0L);
		String wishlistIdTag = (wishlist != null) ? "?id=" + wishlist.getId() : "";
		return "redirect:/receiver" + wishlistIdTag;
	}

}
