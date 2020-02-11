package com.gift_me_five.controller;

import java.security.Principal;
import java.util.ArrayList;
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

import com.gift_me_five.entity.User;
import com.gift_me_five.entity.Wish;
import com.gift_me_five.entity.Wishlist;
import com.gift_me_five.repository.ThemeRepository;
import com.gift_me_five.repository.WishRepository;
import com.gift_me_five.repository.WishlistRepository;
import com.gift_me_five.service.SimpleEmailService;
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

	@Autowired
	private SimpleEmailService simpleEmailService;

	@GetMapping("/giver")
	public String giverWishlistView(Model model, Principal principal, Authentication authentication,
			@RequestParam(required = false) Long id, @RequestParam(required = false) boolean hide,
			@RequestParam(required = false) boolean sort) {

		Wishlist wishlist = userArtifactsService.friendWishlist(id);
		if (wishlist != null) {
			model.addAttribute("myUserId", userArtifactsService.getCurrentUser().getId());
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
			Wishlist myWishlist = userArtifactsService.ownWishlist(wishlist.getId());
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

	@GetMapping("/wishlist/invite")
	public String inviteWishlistGivers(Model model, Principal principal, @RequestParam Long id) {
		Wishlist wishlist = userArtifactsService.ownWishlist(id);
		if (wishlist != null) {
			List<User> givers = wishlist.getGivers();
			String giversList = "";
			for (User giver : givers) {
				giversList += giver.getEmail() + ", ";
			}
			model.addAttribute("wishlistId", id);
			model.addAttribute("giversList", giversList);
			// model.addAttribute("malformed", null);
			return "invite-givers-form";
		}
		// Sollte nur nach Manipulation (e.g. curl) erreicht werden:
		// User versucht, eine wishlist zu verändern, für die er nicht als giver
		// registriert ist
		return "redirect:/under_construction";
	}

	private boolean emailAddressFormatCheck(String emailAddress) {
		boolean format = true;
		String[] emailComponents = emailAddress.split("@");
		if (emailComponents.length > 2) {
			// Must contain exactly one @ character
			format = false;
			System.out.println("Too many @");
		}
		if (!emailComponents[0].matches("\\w[\\w\\.\\-_]*")) {
			// Component before @ must start with word character and contain letters,
			// digits, '.', '_', '-' (to be expanded if needed)
			format = false;
			System.out.println("Name format wrong " + emailComponents[0]);
		}
		// ...
		// further checks to be added!
		System.out.println(emailAddress + " format correct: " + format);
		return format;
	}

	@PostMapping("/wishlist/invite")
	public String addWishlistGivers(Model model, Principal principal, @RequestParam Long id,
			@RequestParam String giversList) {
		Wishlist wishlist = userArtifactsService.ownWishlist(id);
		if (wishlist != null) {
			String[] giversEmails = giversList.strip().split("[,;]");
			List<String> malformedEmails = new ArrayList<>();
			for (String email : giversEmails) {
				if (!emailAddressFormatCheck(email)) {
					malformedEmails.add(email);
				}
			}
			if (malformedEmails.size() > 0) {
				// Request correct address format
				model.addAttribute("wishlistId", id);
				model.addAttribute("malformed", malformedEmails);
				model.addAttribute("giversList", giversList);
				return "invite-givers-form";
			} else {
				// Send out emails to givers
				//
				// get unique key for confirmation URL
				String uuid = wishlist.getUniqueUrlGiver();
				if (uuid == null) {
					uuid = UUID.randomUUID().toString();
					wishlist.setUniqueUrlGiver(uuid);
				}
				String messageBody = "";
				for (String email : giversEmails) {
					try {
						simpleEmailService.emailDummy(email, "Please check out my wishlist!",
								"http://" + request.getLocalName() + ":" + request.getLocalPort() + "/confirm/"
										+ theUser.getEmail() + "/" + theUser.getReason() + "/");
					} catch (Exception ex) {
						return "Error in sending email: " + ex;
					}
				}
					
				}
			}

		}
	// Hier sollte besser eine Meldung auftauchen, dass keine Wishlist angezeigt
	// (Wishlist gehört anderem User)
	// werden kann.
	return"redirect:/under_construction";
}}
