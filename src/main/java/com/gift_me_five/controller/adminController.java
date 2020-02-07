package com.gift_me_five.controller;

import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.gift_me_five.entity.User;
import com.gift_me_five.entity.Wish;
import com.gift_me_five.entity.Wishlist;
import com.gift_me_five.repository.RoleRepository;
import com.gift_me_five.repository.UserRepository;
import com.gift_me_five.repository.WishRepository;
import com.gift_me_five.repository.WishlistRepository;

@Controller
public class adminController {

	@Autowired
	private WishRepository wishRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private WishlistRepository wishlistRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping("/admin/user")
	public String getUser(Model model) {
		model.addAttribute("users", userRepository.findAll());
		return "/admin/get_all_user";
	}

	@GetMapping({ "/admin/new_user", "/admin/edit_user/{id}" })
	public String editUser(Model model, @PathVariable(required = false) Long id) {

		model.addAttribute("roles", roleRepository.findAll());
		if (id == null) {
			model.addAttribute("user", new User());
			return "/admin/edit_user.html";
		}
		Optional<User> optionalUser = userRepository.findById(id);
		if (optionalUser.isPresent()) {
			model.addAttribute("user", optionalUser.get());
		} else {
			return "redirect:/admin/user";
		}
		return "/admin/edit_user";
	}

	@PostMapping("/admin/upsert_user")
	public String upsertUser(Model model, @Valid User user) {
//		old pw != new pw -> encode!

		if (user.getId() == null) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		} else if (!user.getPassword().equals(userRepository.findById(user.getId()).get().getPassword())) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		}

		user = userRepository.save(user);
		return "redirect:/admin/user";
	}

	@GetMapping("/admin/delete_user/{id}")
	public String deleteUser(@PathVariable("id") long id) {
		userRepository.deleteById(id);
		return "redirect:/admin/user";
	}

	@GetMapping("/admin/wish")
	public String getWish(Model model) {
		model.addAttribute("wishes", wishRepository.findAll());
		return "/admin/get_all_wish";
	}

	@GetMapping({ "/admin/edit_wish/{id}" })
//	"/admin/new_wish", no new_wish because of dependencies (wishlistID)
	public String editWish(Model model, @PathVariable(required = false) Long id) {
		if (id == null) {
			model.addAttribute("wish", new Wish());
			return "/admin/edit_wish.html";
		}
		Optional<Wish> optionalWish = wishRepository.findById(id);
		if (optionalWish.isPresent()) {
			model.addAttribute("wish", optionalWish.get());
		} else {
			return "redirect:/admin/wish";
		}
		return "/admin/edit_wish";
	}

	@PostMapping("/admin/upsert_wish")
	public String upsertWish(Model model, @Valid Wish wish) {
		// if no giverID, so set giver to null
		if (wish.getGiver().getId() == null) {
			wish.setGiver(null);
		}
		wish = wishRepository.save(wish);
		return "redirect:/admin/wish";
	}

	@GetMapping("/admin/delete_wish/{id}")
	public String deleteWish(@PathVariable("id") long id) {
		wishRepository.deleteById(id);
		return "redirect:/admin/wish";
	}

	@GetMapping("/admin/wishlist")
	public String getWishlist(Model model) {
		model.addAttribute("wishlists", wishlistRepository.findAll());
		return "/admin/get_all_wishlist";
	}

	@PostMapping("/admin/upsert_wishlist")
	public String upsertWishlist(Model model, @Valid Wishlist wishlist) {
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
		wishlist = wishlistRepository.save(wishlist);
		return "redirect:/admin/wishlist";
	}

	@GetMapping({ "/admin/edit_wishlist/{id}" })
//	"/admin/new_wishlist", no new_wishlist because of dependencies (receiverId)
	public String editWishlist(Model model, @PathVariable(required = false) Long id) {
		if (id == null) {
			model.addAttribute("wishlist", new Wishlist());
			return "/admin/edit_wishlist.html";
		}
		Optional<Wishlist> optionalWishlist = wishlistRepository.findById(id);
		if (optionalWishlist.isPresent()) {
			model.addAttribute("wishlist", optionalWishlist.get());
		} else {
			return "redirect:/admin/wishlist";
		}
		return "/admin/edit_wishlist";
	}

	@GetMapping("/admin/delete_wishlist/{id}")
	public String deleteWishlist(@PathVariable("id") long id) {
		wishlistRepository.deleteById(id);
		return "redirect:/admin/wishlist";
	}
}
