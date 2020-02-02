package com.gift_me_five.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.gift_me_five.entity.User;
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

	@GetMapping("/admin/user")
	public String getUser(Model model) {
		model.addAttribute("users", userRepository.findAll());
		return "/admin/get_all_user";
	}

	@GetMapping({ "/admin/new_user", "/admin/edit_user/{id}" })
	public String editUser(Model model, @PathVariable(required = false) Long id) {
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
	public String upsertuser(Model model, @Valid User user) {
		user = userRepository.save(user);
		return "redirect:/admin/user";
	}

	@GetMapping("/admin/delete_user/{id}")
	public String deleteuser(@PathVariable("id") long id) {
		System.out.println("delete id: " + id);
		userRepository.deleteById(id);
		return "redirect:/admin/user";
	}

	@GetMapping("/admin/wish")
	public String getWish(Model model) {
		model.addAttribute("wishes", wishRepository.findAll());
		return "/admin/get_all_wish";
	}

	@GetMapping("/admin/wishlist")
	public String getWishlist() {
		return "redirect:/under_construction";
	}
}
