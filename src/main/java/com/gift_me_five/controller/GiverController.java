package com.gift_me_five.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.gift_me_five.entity.User;
import com.gift_me_five.entity.Wish;
import com.gift_me_five.repository.UserRepository;
import com.gift_me_five.repository.WishRepository;
import com.gift_me_five.repository.WishlistRepository;

@Controller
public class GiverController {

	@Autowired
	private WishRepository repository;

	@Autowired
	private WishlistRepository wlRepository;

	@Autowired
	private UserRepository uRepository;

	@GetMapping("/giver")
	public String getAll(Model model) {
		model.addAttribute("wishes", repository.findAll());
		return "giver";
	}

	@PostMapping("/giver")
	public String updateWish(@ModelAttribute(value = "wish") Wish wish,
			@ModelAttribute(value = "giverId") Long giverId) {
		//System.out.println("Wish ID = " + wishId);
		wish = repository.findById(wish.getId()).get();
		if (giverId == 0) {
			wish.setGiver(null);
		} else {
			wish.setGiver(uRepository.findById(giverId).get());
		}
		repository.save(wish);
		return "redirect:/giver";
	}
}