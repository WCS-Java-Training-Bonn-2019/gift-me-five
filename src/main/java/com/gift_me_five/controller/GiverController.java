package com.gift_me_five.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.gift_me_five.entity.Wish;
import com.gift_me_five.repository.WishRepository;

@Controller
public class GiverController {
	
	@Autowired
	private WishRepository repository;
		
	@GetMapping("/giver")
	public String getAll(Model model) {
		model.addAttribute("wishes", repository.findAll());		
		return "giver";
	}
	

	@PostMapping("/giver")
	public String updateWish(@ModelAttribute Wish wish) {
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println("ID:" + wish.getId());
		System.out.println("all: " + wish.toString());
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println(repository.save(wish));
		return "redirect:/giver";
	}
}
