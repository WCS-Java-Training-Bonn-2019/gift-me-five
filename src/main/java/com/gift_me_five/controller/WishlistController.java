package com.gift_me_five.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gift_me_five.entity.Wishlist;
import com.gift_me_five.repository.ThemeRepository;
import com.gift_me_five.repository.UserRepository;
import com.gift_me_five.repository.WishlistRepository;

@Controller
public class WishlistController {

	@Autowired
	private WishlistRepository wlRepository;

	@Autowired
	private ThemeRepository thRepository;
	
	@Autowired
	private UserRepository recRepository;

	@GetMapping("/wishlist")
	public String upsertWishList(Model model, @RequestParam(required = false) Long id) {

		Wishlist wishlist = new Wishlist();
//		wishlist.setReceiver(recRepository.findById(id).get());
		// ToDo id Anpassen
		id = 1L;
		if (id != null) {
			Optional<Wishlist> optionalWishlist = wlRepository.findById(id);
			if (optionalWishlist.isPresent()) {
				wishlist = optionalWishlist.get();
			}
		}
		System.out.println(wishlist.getId());
		model.addAttribute("wishlist", wishlist);

		return "wishlist";
	}

	@PostMapping("/wishlist")
	public String saveWishList(@ModelAttribute Wishlist wishlist)  {
 		
		System.out.println("*****Innerhalb von  /wishlist PostMapping ********\n");
		System.out.println("Wishlist Id: " + wishlist.getId());
			
//		wishlist.setReceiver(recRepository.findById(receiverId).get());
//		wishlist.setTheme(thRepository.findById(themeId).get());
		
		wlRepository.save(wishlist);


		return "redirect:/wishlist?id="+wishlist.getId();
	}

	/*
	 * @PostMapping("/wish") public String saveWish(@ModelAttribute Wish wish) {
	 * 
	 * wish.setWishlist(wlRepository.findById(1L).get());
	 * System.out.println(repository.save(wish));
	 * 
	 * return "redirect:/wish?id="+wish.getId(); }
	 * 
	 */

	@GetMapping("/wishlist/delete")
	public String deleteWishList(@RequestParam Long id) {

		wlRepository.deleteById(id);

		return "redirect:/wishlist";
	}

}
