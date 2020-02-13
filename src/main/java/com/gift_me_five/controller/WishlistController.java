package com.gift_me_five.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gift_me_five.GiftMeFive;
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
	private UserRepository userRepository;

	@Autowired
	private ThemeRepository themeRepository;

	@Autowired
	private WishRepository wishRepository;

	@Autowired
	private UserArtifactsService userArtifactsService;

	@GetMapping({ "/giver", "/public/giver/{uniqueUrlGiver}" })
	public String giverWishlistView(Model model, Principal principal, Authentication authentication,
			@RequestParam(required = false) Long id, @RequestParam(required = false) boolean hide,
			@RequestParam(required = false) boolean sort, @PathVariable(required = false) String uniqueUrlGiver) {
		
		Wishlist wishlist = userArtifactsService.friendWishlist(id);
		
		if (id == null && uniqueUrlGiver != null) {
			model.addAttribute("visibility", "public");
			Optional<Wishlist> optionalWishlist = wishlistRepository.findByUniqueUrlGiver(uniqueUrlGiver);
			if (optionalWishlist.isPresent() && optionalWishlist.get().getReceiver().getId() == 2) {
				wishlist = optionalWishlist.get();
			}
		}
		
		if (wishlist != null) {
			if (principal != null) {
				model.addAttribute("myUserId", userArtifactsService.getCurrentUser().getId());
			}
			// Flag to indicate whether wishes should be sorted by price
			model.addAttribute("sort", sort);
			// Flag to indicate whether wishes selected by other friends shall be hidden
			model.addAttribute("hide", hide);
			// Populate menu item for own wishlists (titles needed)
			model.addAttribute("myWishlists", userArtifactsService.allOwnWishlists());
			// Populate menu item for friends wishlists (titles needed)
			model.addAttribute("friendWishlists", userArtifactsService.allFriendWishlists());
			// Title, id and theme of current wishlist needed to build the page:
			model.addAttribute("wishlist", wishlist);
			// model.addAttribute("wishes", wishRepository.findByWishlist(wishlist));
			model.addAttribute("wishes", userArtifactsService.unSelectedWishes(wishlist, sort));
			return "giver";
		}
		// Hier sollte besser eine Meldung auftauchen, dass keine Wishlist angezeigt
		// werden kann.
		return "redirect:/under_construction";
//		GiftMeFive.debugOut("retry", 60);
//		return "redirect:/public/giver/fafc85b1-a07b-4866-9ba8-6d6a2f5d9bc0";

	}

	@PostMapping("/giver")
	public String updateWish(@RequestParam(required = false) Long wishId, @RequestParam(required = false) boolean hide,
			@RequestParam(required = false) boolean sort) {
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
			return "redirect:/giver?id=" + wishlist.getId() + "&hide=" + hide + "&sort=" + sort;
		}
		// Sollte nur nach Manipulation (e.g. curl) erreicht werden:
		// User versucht, eine wishlist zu verändern, für die er nicht als giver
		// registriert ist
		return "redirect:/under_construction";
	}

	@GetMapping({ "/public/receiver", "/public/receiver/{uniqueUrlReceiver}" })
	public String displayPublicWishlist(Principal principal, Model model, HttpServletRequest request,
			@PathVariable(required = false) String uniqueUrlReceiver) {

		// only Anonymous user should be here
		if (principal != null) {
			return "redirect:/";
		}

		model.addAttribute("visibility", "public");
		Wishlist wishlist = new Wishlist();

		if (uniqueUrlReceiver != null
				&& wishlistRepository.findByUniqueUrlReceiver(uniqueUrlReceiver).getReceiver().getId() == 2) {
			wishlist = wishlistRepository.findByUniqueUrlReceiver(uniqueUrlReceiver);
		} else {
			return "redirect:/public/wishlist";
		}

		model.addAttribute("thisWishlistId", wishlist.getId());
		model.addAttribute("wishlist", wishlist);
		model.addAttribute("wishes", wishRepository.findByWishlist(wishlist));

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

	@GetMapping({ "/public/wishlist", "/public/wishlist/{uniqueUrlReceiver}" })
	public String newPublicWishlist(Model model, Principal principal,
			@PathVariable(required = false) String uniqueUrlReceiver) {
		// only Anonymous user should be here
		if (principal != null) {
			return "redirect:/wishlist";
		}

		model.addAttribute("visibility", "public");
		model.addAttribute("themes", themeRepository.findAll());

		if (uniqueUrlReceiver != null
				&& wishlistRepository.findByUniqueUrlReceiver(uniqueUrlReceiver).getReceiver().getId() == 2) {
			// edit wishlist uniqueUrlReceiver
			Wishlist wishlist = wishlistRepository.findByUniqueUrlReceiver(uniqueUrlReceiver);
			model.addAttribute("wishlist", wishlist);
			return "wishlist";
		}

		// new public wishlist
		Wishlist wishlist = new Wishlist();
		wishlist.setReceiver(userRepository.findById(2L).get());
		wishlist.setTheme(themeRepository.findById(1L).get()); // default theme set
		model.addAttribute("wishlist", wishlist);
		return "wishlist";
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

	@PostMapping({ "/wishlist", "/public/wishlist" })
	public String saveWishList(@ModelAttribute Wishlist wishlist, @RequestParam("submit") String submit) {

		if (wishlist.getId() == null) {

//			wishlist.setReceiver(userArtifactsService.getCurrentUser());
			User current = userArtifactsService.getCurrentUser();
			if (current == null) { // no current, so anonymous -> default public user
				current = userRepository.findById(2L).get();
			}
//				wishlist.setReceiver(userArtifactsService.getCurrentUser());
			wishlist.setReceiver(current);
			// if null or empty, create UUID as uniqueUrlReceiver
			if (wishlist.getUniqueUrlReceiver() == null || wishlist.getUniqueUrlReceiver().isEmpty()) {
				String uniqueUrlReceiver;
				do {
					uniqueUrlReceiver = UUID.randomUUID().toString();
				} while (wishlistRepository.findByUniqueUrlReceiver(uniqueUrlReceiver) != null);
				wishlist.setUniqueUrlReceiver(uniqueUrlReceiver);
			}

			// if null or empty, create UUID as uniqueUrlGiver
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
			// unless public
			Wishlist myWishlist = userArtifactsService.ownWishlist(wishlist.getId());

			if (myWishlist == null) {
				myWishlist = wishlistRepository.findById(wishlist.getId()).get();
			}

			if (myWishlist != null) {
				myWishlist.setTheme(wishlist.getTheme());
				myWishlist.setTitle(wishlist.getTitle());
				wishlist = myWishlist;

				// if null or empty, create UUID as uniqueUrlReceiver
				if (wishlist.getUniqueUrlReceiver() == null || wishlist.getUniqueUrlReceiver().isEmpty()) {
					String uniqueUrlReceiver;
					do {
						uniqueUrlReceiver = UUID.randomUUID().toString();
					} while (wishlistRepository.findByUniqueUrlReceiver(uniqueUrlReceiver) != null);
					wishlist.setUniqueUrlReceiver(uniqueUrlReceiver);
				}

				// if null or empty, create UUID as uniqueUrlGiver
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

		if (wishlist.getReceiver().getId() != 2) {
			return "redirect:/receiver?id=" + wishlist.getId();
		} else {
			return "redirect:/public/receiver/" + wishlist.getUniqueUrlReceiver();
		}
	}

	@GetMapping({ "/wishlist/delete", "/public/wishlist/delete" })
	public String deleteWishList(@RequestParam Long id) {
		// Check if own wishlist - otherwise don't delete
		// unless its public
		if (userArtifactsService.ownWishlist(id) != null
				|| wishlistRepository.findById(id).get().getReceiver().getId() == 2) {
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
