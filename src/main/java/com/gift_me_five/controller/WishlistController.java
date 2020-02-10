package com.gift_me_five.controller;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

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
	private WishRepository wishRepository;

	@Autowired
	private UserArtifactsService userArtifactsService;

	@GetMapping("/giver")
	public String giverWishlistView(Model model, Principal principal, Authentication authentication,
			@RequestParam(required = false) Long id, @RequestParam(required = false) boolean hide) {

		Wishlist wishlist = userArtifactsService.friendWishlist(id);
		if (wishlist != null) {
			model.addAttribute("myUserId", userArtifactsService.getCurrentUser().getId());
            model.addAttribute("hide", hide);
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
	public String updateWish(@RequestParam(required = false) Long wishId, @RequestParam(required = false) boolean hide) {
		// System.out.println("Wish ID = " + wishId);
		
		Wish wish = userArtifactsService.friendWish(wishId);
		if (wish != null) {
			Wishlist wishlist = wish.getWishlist();
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
	public String displayWishlist(Model model, @RequestParam(required = false) Long id, HttpServletRequest request) {
	
//		hostname (e.g. localhost)
//		GiftMeFive.debugOut(request.getLocalName());
//		ip address (e.g. 127.0.0.1)
//		GiftMeFive.debugOut(request.getLocalAddr());
//		GiftMeFive.debugOut(request.getLocalPort());
//		GiftMeFive.debugOut(request.getProtocol());
		Wishlist wishlist = new Wishlist();
		if (id != null && userArtifactsService.ownWishlist(id) != null) {
			wishlist = userArtifactsService.ownWishlist(id);
		} else {
			return ("redirect:/wishlist");
		}
		model.addAttribute("thisWishlistId", wishlist.getId());
		model.addAttribute("wishlist", wishlist);
		model.addAttribute("myWishlists", userArtifactsService.allOwnWishlists());
		model.addAttribute("friendWishlists", userArtifactsService.allFriendWishlists());
		model.addAttribute("wishes", wishRepository.findByWishlist(wishlist));
		// todo: add protocol to model (invite url, receiver.html)
		model.addAttribute("hostname", request.getLocalName());
		model.addAttribute("port", request.getLocalPort());
		return ("receiver");
	}

	@GetMapping({ "/wishlist", "/newwishlist" })
	public String upsertWishList(Model model, Principal principal, @RequestParam(required = false) Long id) {

		Wishlist wishlist = new Wishlist();

		// Anonymous user should not be allowed to do /wishlist?id=x
		if (principal == null && id != null) {
			return "redirect:/wishlist";
		}

		// If wishlist exists and belongs to logged in user, update it. Else create a
		// new one.
		if (id != null && userArtifactsService.ownWishlist(id) != null) {
			wishlist = userArtifactsService.ownWishlist(id);
		} else {
			id = null;
			Long themeId = 1L; // theme Id default value for new wishlist
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
	public String saveWishList(@ModelAttribute Wishlist wishlist, @RequestParam("submit") String submit) {

		if (wishlist.getId() == null) {
			
			wishlist.setReceiver(userArtifactsService.getCurrentUser());
			
			//if null or empty, create UUID as uniqueUrlReceiver
			if (wishlist.getUniqueUrlReceiver() == null || wishlist.getUniqueUrlReceiver().isEmpty()) {
				String uniqueUrlReceiver;
				do {
				uniqueUrlReceiver = UUID.randomUUID().toString();
				} while (wishlistRepository.findByUniqueUrlReceiver(uniqueUrlReceiver) != null); 
				wishlist.setUniqueUrlReceiver(uniqueUrlReceiver);
			}

			//if null or empty, create UUID as uniqueUrlGiver
			if (wishlist.getUniqueUrlGiver() == null || wishlist.getUniqueUrlGiver().isEmpty()) {
				String uniqueUrlGiver;
				do {
				uniqueUrlGiver = UUID.randomUUID().toString();
				} while (wishlistRepository.findByUniqueUrlReceiver(uniqueUrlGiver) != null); 
				wishlist.setUniqueUrlGiver(uniqueUrlGiver);
			}

			wishlistRepository.save(wishlist);
		} else {
			// Check if own wishlist; only then modify.
			// Only take over title and theme
			Wishlist myWishlist = userArtifactsService.ownWishlist(wishlist.getId());
			if (myWishlist != null) {
				myWishlist.setTheme(wishlist.getTheme());
				myWishlist.setTitle(wishlist.getTitle());
				wishlist = myWishlist;
				
				//if null or empty, create UUID as uniqueUrlReceiver
				if (wishlist.getUniqueUrlReceiver() == null || wishlist.getUniqueUrlReceiver().isEmpty()) {
					String uniqueUrlReceiver;
					do {
					uniqueUrlReceiver = UUID.randomUUID().toString();
					} while (wishlistRepository.findByUniqueUrlReceiver(uniqueUrlReceiver) != null); 
					wishlist.setUniqueUrlReceiver(uniqueUrlReceiver);
				}				

				//if null or empty, create UUID as uniqueUrlGiver
				if (wishlist.getUniqueUrlGiver() == null || wishlist.getUniqueUrlGiver().isEmpty()) {
					String uniqueUrlGiver;
					do {
					uniqueUrlGiver = UUID.randomUUID().toString();
					} while (wishlistRepository.findByUniqueUrlReceiver(uniqueUrlGiver) != null); 
					wishlist.setUniqueUrlGiver(uniqueUrlGiver);
				}
				
				wishlistRepository.save(wishlist);
			}
		}

		if ("Add Wish".equals(submit)) {
			return "redirect:/wish?wishlistId=" + wishlist.getId();
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
