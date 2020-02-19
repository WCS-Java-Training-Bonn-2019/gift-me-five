package com.gift_me_five.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gift_me_five.entity.User;
import com.gift_me_five.entity.Wish;
import com.gift_me_five.entity.Wishlist;
import com.gift_me_five.repository.ThemeRepository;
import com.gift_me_five.repository.UserRepository;
import com.gift_me_five.repository.WishRepository;
import com.gift_me_five.repository.WishlistRepository;
import com.gift_me_five.service.SimpleEmailService;
import com.gift_me_five.service.UserArtifactsService;

@Controller
@ControllerAdvice
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

	@Autowired
	private SimpleEmailService simpleEmailService;

	@ModelAttribute
	public void addMenuItems(Model model) {
		// Populate menu item for own wishlists (titles needed)
		model.addAttribute("myWishlists", userArtifactsService.getAllMyWishlistsAsReceiver());
		// Populate menu item for friends wishlists (titles needed)
		model.addAttribute("friendWishlists", userArtifactsService.getAllMyWishlistsAsGiver());

	}

	@GetMapping({ "/giver", "/public/giver/{uniqueUrlGiver}" })
	public String giverWishlistView(Model model, Principal principal, Authentication authentication,
			@RequestParam(required = false) Long id, @RequestParam(required = false) boolean hide,
			@RequestParam(required = false) boolean sort, @PathVariable(required = false) String uniqueUrlGiver) {

		Wishlist wishlist = userArtifactsService.getWishlistIfGiver(id);

		if (id == null && uniqueUrlGiver != null) {
			model.addAttribute("visibility", "public");
			model.addAttribute("imagePath", "public/wish/" + uniqueUrlGiver + "/");
			Optional<Wishlist> optionalWishlist = wishlistRepository.findByUniqueUrlGiver(uniqueUrlGiver);
			if (optionalWishlist.isPresent() && optionalWishlist.get().getReceiver().getId() == 2) {
				wishlist = optionalWishlist.get();
			}
		}

		if (wishlist != null) {
			if (principal != null) {
				model.addAttribute("myUserId", userArtifactsService.getCurrentUser().getId());
				model.addAttribute("imagePath", "wish/");
			} // else {
//				model.addAttribute("visibility", "public");
//			}
			// Flag to indicate whether wishes should be sorted by price
			model.addAttribute("sort", sort);
			// Flag to indicate whether wishes selected by other friends shall be hidden
			model.addAttribute("hide", hide);
			// Title, id and theme of current wishlist needed to build the page:
			model.addAttribute("wishlist", wishlist);
			// model.addAttribute("wishes", wishRepository.findByWishlist(wishlist));
			model.addAttribute("wishes", userArtifactsService.listUnSelectedWishesForGiver(wishlist, sort));
			return "giver";
		}

		return "redirect:/not_authorized";

	}
	
	@PostMapping("/public/giver/{uniqueUrlGiver}")
	public String updatePublicWish(@RequestParam(required = false) Long wishId, @PathVariable(required = false) String uniqueUrlGiver, @RequestParam(required = false) boolean hide,
			@RequestParam(required = false) boolean sort) {

		System.out.println("Wish ID = " + wishId);

		Wish wish = userArtifactsService.getWishIfGiver(wishId);
		if (wish != null) {
//			Wishlist wishlist = wish.getWishlist();
			if (wish.getGiver() == null) {
				wish.setGiver(userRepository.findById(2L).get());
			} else if (userArtifactsService.getCurrentUser() == wish.getGiver()) {
				wish.setGiver(null);
			}
			wishRepository.save(wish);
			return "redirect:/public/giver/" + uniqueUrlGiver + "?hide=" + hide + "&sort=" + sort;		}

		return "redirect:/not_authorized";
	}
	
	@PostMapping("/giver")
	public String updateWish(@RequestParam(required = false) Long wishId, @RequestParam(required = false) boolean hide,
			@RequestParam(required = false) boolean sort) {
		 System.out.println("Wish ID = " + wishId);

		Wish wish = userArtifactsService.getWishIfGiver(wishId);
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

		return "redirect:/not_authorized";
	}

	@GetMapping("/public/receiver/{uniqueUrlReceiver}")
	public String displayPublicWishlist(Principal principal, Model model, HttpServletRequest request,
			@PathVariable(required = false) String uniqueUrlReceiver) {

		model.addAttribute("visibility", "public");
		Wishlist wishlist = userArtifactsService.getWishlistIfPublic(uniqueUrlReceiver);

		if (wishlist == null) {
			return "redirect:/not_authorized";
		}

		model.addAttribute("imagePath", "public/wish/" + uniqueUrlReceiver + "/");
		model.addAttribute("thisWishlistId", wishlist.getId());
		model.addAttribute("wishlist", wishlist);
		model.addAttribute("wishes", wishRepository.findByWishlist(wishlist));
		
		// todo: add protocol to model (invite url, receiver.html)
		model.addAttribute("hostname", request.getLocalName());
		model.addAttribute("port", request.getLocalPort());
		return ("receiver");
	}

	@GetMapping("/receiver")
	public String displayWishlist(Model model, @RequestParam(required = false) Long id, HttpServletRequest request) {

		Wishlist wishlist = new Wishlist();
		if (id != null && userArtifactsService.getWishlistIfReceiver(id) != null) {
			wishlist = userArtifactsService.getWishlistIfReceiver(id);
		} else {
			return ("redirect:/wishlist");
		}
		model.addAttribute("imagePath", "wish/");
		model.addAttribute("thisWishlistId", wishlist.getId());
		model.addAttribute("wishlist", wishlist);
		model.addAttribute("wishes", wishRepository.findByWishlist(wishlist));
		// todo: add protocol to model (invite url, receiver.html)
		model.addAttribute("hostname", request.getLocalName());
		model.addAttribute("port", request.getLocalPort());
		return ("receiver");
	}

	@GetMapping({ "/public/wishlist", "/public/wishlist/{uniqueUrlReceiver}" })
	public String newPublicWishlist(Model model, Principal principal,
			@PathVariable(required = false) String uniqueUrlReceiver) {

		model.addAttribute("visibility", "public");
		model.addAttribute("themes", themeRepository.findAll());

		if (uniqueUrlReceiver != null && wishlistRepository.findByUniqueUrlReceiver(uniqueUrlReceiver).isPresent()) {
			Wishlist wishlist = wishlistRepository.findByUniqueUrlReceiver(uniqueUrlReceiver).get();
			if (wishlist.getReceiver().getId() == 2) {
				model.addAttribute("wishlist", wishlist);
				return "wishlist";
			} else {
				return "redirect:/not_authorized";
			}
		}

		// new public wishlist
		Wishlist wishlist = new Wishlist();
		wishlist.setReceiver(userRepository.findById(2L).get());
		wishlist.setTheme(themeRepository.findById(1L).get()); // default theme set
		model.addAttribute("wishlist", wishlist);
		return "wishlist";
	}

	@GetMapping("/wishlist")
	public String upsertWishList(Model model, Principal principal, @RequestParam(required = false) Long id) {

		Wishlist wishlist = new Wishlist();

		// If wishlist exists and belongs to logged in user, update it. Else create a
		// new one.
		if (id != null && userArtifactsService.getWishlistIfReceiver(id) != null) {
			wishlist = userArtifactsService.getWishlistIfReceiver(id);
		} else {
			id = null;
			Long themeId = 1L; // theme Id default value for new wishlist
			wishlist.setReceiver(userArtifactsService.getCurrentUser());
			wishlist.setTheme(themeRepository.findById(themeId).get());
		}

		model.addAttribute("wishlist", wishlist);
		model.addAttribute("themes", themeRepository.findAll());
		return "wishlist";
	}

	@PostMapping("/wishlist")
	public String savePrivateWishList(@ModelAttribute Wishlist wishlist, @RequestParam("submit") String submit) {

		Wishlist myWishlist = userArtifactsService.getWishlistIfReceiver(wishlist.getId());

		if (myWishlist == null) {

			if (wishlist.getId() == null) {
				// New wishlist!
				wishlist.setReceiver(userArtifactsService.getCurrentUser());
				wishlist.setUniqueUrlReceiver(UUID.randomUUID().toString());
				wishlist.setUniqueUrlGiver(UUID.randomUUID().toString());
				wishlistRepository.save(wishlist);
			} else { // wishlist id not allowed for this user
				wishlist.setId(null);
			}
		} else { // wishlist belongs to this user - update.
			// Only take over title and theme
			myWishlist.setTheme(wishlist.getTheme());
			myWishlist.setTitle(wishlist.getTitle());
			wishlist = myWishlist;
			wishlistRepository.save(wishlist);
		}

		if (wishlist.getId() != null) {
			if ("Add Wish".equals(submit)) {
				return "redirect:/wish?wishlistId=" + wishlist.getId();
			}
			return "redirect:/receiver?id=" + wishlist.getId();
		}

		return "redirect:/not_authorized";
	}

	@PostMapping("/public/wishlist/{uniqueUrlReceiver}")
	public String savePublicWishlist(@PathVariable String uniqueUrlReceiver, @ModelAttribute Wishlist wishlist,
			@RequestParam("submit") String submit) {

		System.out.println(uniqueUrlReceiver);

		if (wishlist.getId() == null && "null".equals(uniqueUrlReceiver)) {

			// New wishlist! Need to identify receiver.
			User current = userRepository.findById(2L).get();
			wishlist.setReceiver(current);
			uniqueUrlReceiver = UUID.randomUUID().toString();
			wishlist.setUniqueUrlReceiver(uniqueUrlReceiver);
			wishlist.setUniqueUrlGiver(UUID.randomUUID().toString());

			wishlistRepository.save(wishlist);
		} else if (uniqueUrlReceiver != null) {
			// Check if own wishlist; only then modify.
			// Only take over title and theme
			Wishlist myWishlist = userArtifactsService.getWishlistIfPublic(uniqueUrlReceiver);

			if (myWishlist != null && myWishlist.getId().equals(wishlist.getId())) {
				myWishlist.setTheme(wishlist.getTheme());
				myWishlist.setTitle(wishlist.getTitle());
				wishlist = myWishlist;

				wishlistRepository.save(wishlist);
			} else { // mismatch between uniqueUrlReceiver and wishlist Id
				uniqueUrlReceiver = null;
			}
		}
		if (uniqueUrlReceiver != null) {
			if ("Add Wish".equals(submit)) {
				return "redirect:/public/wish/" + uniqueUrlReceiver;
			}
			return "redirect:/public/receiver/" + uniqueUrlReceiver;
		}

		return "redirect:/not_authorized";
	}

	@GetMapping({ "/wishlist/delete", "/public/wishlist/delete" })
	public String deleteWishList(@RequestParam Long id) {
		// Check if own wishlist - otherwise don't delete
		// unless its public
		if (userArtifactsService.getWishlistIfReceiver(id) != null
				|| wishlistRepository.findById(id).get().getReceiver().getId() == 2) {
			wishlistRepository.deleteById(id);
		}
		// redirect to another own wishlist if exists,
		// otherwise to new wishlist page.
		List<Wishlist> myWishlist = userArtifactsService.getAllMyWishlistsAsReceiver();
		if (myWishlist.size() > 0) {
			return "redirect:/receiver?id=" + myWishlist.get(0).getId();
		} else {
			return "redirect:/newwishlist";
		}
	}

	// ***********************************************************************************
	// TODO:
	// Invite for public wishlists
	// ***********************************************************************************

	@GetMapping("/wishlist/invite")
	public String inviteWishlistGivers(Model model, Principal principal, @RequestParam Long id) {
		Wishlist wishlist = userArtifactsService.getWishlistIfReceiver(id);
		if (wishlist != null) {
			model.addAttribute("wishlistId", id);
			return "invite-givers-form";
		}

		return "redirect:/not_authorized";
	}

	@PostMapping("/wishlist/invite")
	public String addWishlistGivers(Model model, Principal principal, HttpServletRequest request,
			@RequestParam String giversList, @RequestParam Long id) {

		Wishlist wishlist = userArtifactsService.getWishlistIfReceiver(id);

		if (wishlist != null) {
			
			List<String> malformedEmails = simpleEmailService.sendInviteEmails(wishlist, giversList);
						
			if (malformedEmails.isEmpty()) {
				model.addAttribute("invitationSent", true);
			} else {
				// There are some malformed email addresses!
				// Request correct address format
				model.addAttribute("malformed", malformedEmails);
				model.addAttribute("invitationSent", false);
			}
			model.addAttribute("wishlistId", id);
			model.addAttribute("giversList", giversList);
			return "invite_givers_form";
		}

	return"redirect:/not_authorized";

	}

	// Mapping for invitation accept -- User must be logged in!
	@GetMapping("/public/wishlist/invite/{uuid}")
	public String confirmEmail(Model model, Principal principal, HttpServletRequest request,
			HttpServletResponse response, @PathVariable(required = true) String uuid) {

		User newGiver = userArtifactsService.getCurrentUser();
		if (newGiver == null) {
			// User gets a message that login or registration is required.
			Cookie cookie = new Cookie("invite", uuid);
			cookie.setMaxAge(2 * 24 * 60 * 60);
			cookie.setPath("/");
			response.addCookie(cookie);
			return "login_or_register";
		}
		// TODO:
		// Replace by Optional<Wishlist>!!!
		Wishlist wishlist = wishlistRepository.findByUniqueUrlGiver(uuid).get();
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
