package com.gift_me_five.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.gift_me_five.entity.Wish;
import com.gift_me_five.repository.UserRepository;
import com.gift_me_five.repository.WishRepository;

@Controller
public class GiverController {

	@Autowired
	private WishRepository repository;

	@Autowired
	private UserRepository userRepository;

	@GetMapping("/giver")
	public String getAll(Model model) {
//		model.addAttribute("wishes", repository.findAll());
		model.addAttribute("wishes", repository.findByIdLessThan(5L));
//		model.addAttribute("wishes", repository.findById(2L));
		System.out.println();System.out.println();
		System.out.println();System.out.println();
		System.out.println(repository.toString());
		System.out.println();System.out.println();
		System.out.println();System.out.println();
		return "giver";
	}

	@PostMapping("/giver")
	public String updateWish(@ModelAttribute(value = "wishId") Long wishId,
			@ModelAttribute(value = "giverId") Long giverId) {
		//System.out.println("Wish ID = " + wishId);
		Wish wish = repository.findById(wishId).get();
		if (giverId == 0) {
			wish.setGiver(null);
		} else {
			wish.setGiver(userRepository.findById(giverId).get());
		}
		repository.save(wish);
		return "redirect:/giver";
	}
}
