package com.gift_me_five.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gift_me_five.GiftMeFive;
import com.gift_me_five.entity.Theme;
import com.gift_me_five.entity.User;
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
		if (wishlist != null) {
			model.addAttribute("myUserId", userArtifactsService.getCurrentUser().getId());
			model.addAttribute("thisWishlistId", wishlist.getId());
			model.addAttribute("myWishlists", userArtifactsService.allOwnWishlists());
			model.addAttribute("friendWishlists", userArtifactsService.allFriendWishlists());
			model.addAttribute("wishlist", wishlist);
			model.addAttribute("wishes", wishRepository.findByWishlist(wishlist));
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
		// User versucht, eine wishlist zu verändern, für die er nicht als giver
		// registriert ist
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
		model.addAttribute("thisWishlistId", wishlist.getId());
		model.addAttribute("wishlist", wishlist);
		model.addAttribute("myWishlists", userArtifactsService.allOwnWishlists());
		model.addAttribute("friendWishlists", userArtifactsService.allFriendWishlists());
		model.addAttribute("wishes", wishRepository.findByWishlist(wishlist));
		return ("receiver");
	}

	@GetMapping({ "/wishlist", "/newwishlist" })
	public String upsertWishList(Model model, Principal principal, @RequestParam(required = false) Long id) {

		Wishlist wishlist = new Wishlist();

		// *****************************************************
		// TO DO: Default values must be defined!!!
		// Wishlists of current user must be added to model instead of all wishlists!
		// *****************************************************
		Long themeId = 1L; // theme Id default Wert
		
		//anonymous should not be allowed to do /wishlist?id=x
		if (principal == null) {
			id = null;
		}
		
		//todo: edit wishlist (/wishlist?id=x) should be limited to own wishlists
		
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
			wishlist.setReceiver(userArtifactsService.getCurrentUser());
			wishlist.setTheme(themeRepository.findById(themeId).get());
		}

		model.addAttribute("myWishlists", userArtifactsService.allOwnWishlists());
		model.addAttribute("friendWishlists", userArtifactsService.allFriendWishlists());
		model.addAttribute("wishlist", wishlist);
		model.addAttribute("themes", themeRepository.findAll());
		return "wishlist";
	}

	@PostMapping("/wishlist")
	public String saveWishList(@ModelAttribute Wishlist wishlist) {

		User receiver = userArtifactsService.getCurrentUser();
		if (wishlist.getId() == null) {
			wishlist.setReceiver(receiver);
			wishlistRepository.save(wishlist);
		} else {
			// Check if own wishlist; only then modify
			// Only take over title and theme
			Wishlist myWishlist = userArtifactsService.ownWishlist(wishlist.getId());
			if (myWishlist != null) {
				myWishlist.setTheme(wishlist.getTheme());
				myWishlist.setTitle(wishlist.getTitle());
				wishlist = myWishlist;
				wishlistRepository.save(wishlist);
			}

		}
        
		return "redirect:/receiver?id=" + wishlist.getId();
	}

	@GetMapping("/wishlist/delete")
	public String deleteWishList(@RequestParam Long id) {
        // Check if own wishlist - otherwise don't delete
		if (userArtifactsService.ownWishlist(id) != null) {
    		wishlistRepository.deleteById(id);
        }
		// redirect to another own wishlist if exists,
		// otherwise to new wishlist page.
		List<Wishlist> myWishlist = userArtifactsService.allOwnWishlists();
        if (myWishlist.size() > 0) {
    		return "redirect:/receiver?id=" + myWishlist.get(0).getId();
        } else {
        	return "redirect:/newwishlist";
        }
	}

}
