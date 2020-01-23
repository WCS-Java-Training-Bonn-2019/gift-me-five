package com.gift_me_five.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.gift_me_five.repository.WishRepository;

@Controller
public class GiverController {
	
	@Autowired
	private WishRepository repository;
		
	@GetMapping("/giver")
	public String getAll(Model model) {
		model.addAttribute("wishes", repository.findAll());
		System.out.println(repository.findAll());
		return "giver";
	}
}
