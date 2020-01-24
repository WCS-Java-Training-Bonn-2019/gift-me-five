package com.gift_me_five.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gift_me_five.entity.Wish;
import com.gift_me_five.entity.Wishlist;
import com.gift_me_five.repository.WishRepository;
import com.gift_me_five.repository.WishlistRepository;

@Controller
public class WishController {
	
	@Autowired
	private WishRepository repository;
	
	@Autowired
	private WishlistRepository wlRepository;
	
	@GetMapping("/wish")
	public String upsertWish(Model model, @RequestParam(required = false) Long id) {
		
		Wish wish = new Wish();
		if (id != null) {
			Optional<Wish> optionalWish = repository.findById(id);
			if (optionalWish.isPresent()) {
				wish = optionalWish.get();
			}
		}
		model.addAttribute("wish", wish);
		
		return "wishForm";
	}
	
	@PostMapping("/wish")
	public String saveWish(@ModelAttribute Wish wish) {
		
		wish.setWishlist(wlRepository.findById(1L).get());
		System.out.println(repository.save(wish));
		
		return "redirect:/wish?id="+wish.getId();
	}

	@GetMapping("/wish/delete")
	public String deleteWish(@RequestParam Long id) {
		
		repository.deleteById(id);
		
		return "redirect:/wish";
	}

}