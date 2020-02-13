package com.gift_me_five.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
		// TODO:
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
		// TODO:
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
			//model.addAttribute("giversList", giversList);
			// model.addAttribute("malformed", null);
			return "invite-givers-form";
		}
		// Sollte nur nach Manipulation (e.g. curl) erreicht werden:
		// User versucht, eine wishlist zu verändern, für die er nicht als giver
		// registriert ist
		return "redirect:/under_construction";
	}

	private boolean emailAddressFormatCheck(String emailAddress) {

		String[] emailComponents = emailAddress.split("@");
		if (emailComponents.length > 2) {
			// Must contain exactly one @ character
			System.out.println("Too many @");
			return false;
		}
		if (!emailComponents[0].matches("\\w[\\w\\.\\_\\-]*")) { //
			// Component before @ must start with word character and contain letters,
			// digits, '.', '_', '-' (to be expanded if needed)
			System.out.println("/" + emailComponents[0] + "/ : Name format wrong ");
			return false;
		}
		String[] domainComponents = emailComponents[1].split("\\.");
		if (domainComponents.length < 2) {
			// Must contain at least one . character
			System.out.println("/" + emailComponents[1] + "/ : Top Level Domain not specified");
			return false;
		}
		if (!domainComponents[domainComponents.length - 1].matches("[A-Za-z]{2,4}")) {
			// TLD only letters and two to four characters
			System.out.println("/" + domainComponents[domainComponents.length - 1] + "/ : Top Level Domain format wrong");
			return false;
		}
		for (String domainComponent : domainComponents) {
			if (!domainComponent.matches("[\\w\\-_]+")) {
				// domain components only contain letters, numbers, - and _
				System.out.println("/" + domainComponent + "/ : Domain component format wrong");
				return false;
			}
		}
		// ...
		// further checks to be added!
		return true;
	}

	@PostMapping("/wishlist/invite")
	public String addWishlistGivers(Model model, Principal principal, HttpServletRequest request, @RequestParam String giversList, @RequestParam Long id) {

		Wishlist wishlist = userArtifactsService.ownWishlist(id);
		System.out.println("***" + giversList + "***");
		if (wishlist != null) {
			String[] giversEmails = giversList.strip().split("[,;]");
			List<String> malformedEmails = new ArrayList<>();
			for (String email : giversEmails) {
				email = email.strip();
				if (!emailAddressFormatCheck(email)) {
					malformedEmails.add(email);
				}
			}

			if (malformedEmails.size()== 0) {
				// Send out emails to givers
				String uuid = wishlist.getUniqueUrlGiver();
				String subject = "Please check out my wishlist!";
				String messageBody = "Hi,\n" + "I'm " + userArtifactsService.getCurrentUser().getFirstname()
						+ " and I would like to invite you to my new wishlist: \n\n" + "http://" + "localhost:8080"
						+ "/wishlist/invite/" + uuid + "/";
				// + request.getLocalName() + ":" + request.getLocalPort() + "/wishlist/invite/"
				// + uuid + "/";
				for (String email : giversEmails) {
					try {
						simpleEmailService.emailDummy(email, subject, messageBody);
					} catch (Exception ex) {
						System.out.println("Error in sending email: " + ex);
					}
				}
				// Inform user about success
				model.addAttribute("invitationSent", true);
			} else {
				// There are some malformed email addresses!
				// Request correct address format
				model.addAttribute("malformed", malformedEmails);
				model.addAttribute("invitationSent", false);
			}
			model.addAttribute("wishlistId", id);
			model.addAttribute("giversList", giversList);
			return "invite-givers-form";
		}
        // TODO:
		// Hier sollte besser eine Meldung auftauchen, dass keine Wishlist angezeigt
		// (Wishlist gehört anderem User)
		// werden kann.
		return "redirect:/under_construction";
	}

	// Mapping for invitation accept -- User must be logged in!
	@GetMapping("/wishlist/invite/{uuid}")
	public String confirmEmail(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response,
			@PathVariable(required = true) String uuid) {

		User newGiver = userArtifactsService.getCurrentUser();
		if (newGiver == null) {
			// User gets a message that login or registration is required.
			Cookie cookie = new Cookie("invite", uuid);
			cookie.setMaxAge(2*24*60*60);
			cookie.setPath("/");
			response.addCookie(cookie);
			return "login_or_register";
		}
		Wishlist wishlist = wishlistRepository.findByUniqueUrlGiver(uuid);
		if (wishlist == null) {
			// TODO:
			// User should get a message that the unique URL is invalid.
			return "invalid_giver_uuid";
		}
		if (!wishlist.getGivers().contains(newGiver)) {

			wishlist.getGivers().add(newGiver);
			wishlistRepository.save(wishlist);
		}

		return "redirect:/giver?id=" + wishlist.getId();

	}

}
