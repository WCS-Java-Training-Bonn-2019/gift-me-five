package com.gift_me_five.controller;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gift_me_five.GiftMeFive;
import com.gift_me_five.entity.User;
import com.gift_me_five.repository.UserRepository;
import com.gift_me_five.service.SimpleEmailService;
import com.gift_me_five.service.UserArtifactsService;

@Controller
public class LoginController {

	@Autowired
	SimpleEmailService simpleEmailSerive;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	UserArtifactsService userArtifactsService;

	@GetMapping("/")
	public String showStartPage(Model model, Principal principal, @RequestParam(required = false) Long loginFailure) {

		model.addAttribute("myWishlists", userArtifactsService.allOwnWishlists());
		model.addAttribute("friendWishlists", userArtifactsService.allFriendWishlists());
		model.addAttribute("loginFailure", loginFailure);

//		if (principal != null) {
//			GiftMeFive.debugOut(principal.getName());
//		} else {
//			GiftMeFive.debugOut("no principal, yet!!!");
//		}

		return "index";
	}

	@GetMapping("/forgot_password")
	public String forgotPassword() {
		return "forgot_password";
	}

	@PostMapping("/forgot_password/confirm")
	public String sendRecoveryEmail(String email, HttpServletRequest request) {

		Optional<User> user = userRepository.findByEmail(email);
		if (!user.isEmpty()) {
			User existing = user.get();
			existing.setReason(UUID.randomUUID().toString());
			userRepository.save(existing);
			simpleEmailSerive.email(existing.getEmail(), "Reset PW",
					"http://" + request.getLocalName() + ":" + request.getLocalPort() + "/reset/" + existing.getEmail()
							+ "/" + existing.getReason() + "/");

		}
		// not showing if email is not existing, no clues!
//		return "redirect:/forgot_password";
		return "forgot_password_confirm";
	}
	
	@PostMapping("/reset")
	public String finishReset(@Valid @ModelAttribute("user") User user, Model model, Principal principal) {
		Optional<User> existing = userRepository.findById(user.getId());
		if (existing.isPresent()) {
			User confirmedUser = existing.get();
			confirmedUser.setPassword(passwordEncoder.encode(user.getPassword()));
			confirmedUser.setFailedLogins(0L);
			confirmedUser.setReason(null);
			userRepository.save(confirmedUser);

			Authentication authentication = new UsernamePasswordAuthenticationToken(confirmedUser, confirmedUser.getPassword(),
					confirmedUser.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);
			return "redirect:/";
		} else {
		return "redirect:/?loginFailure=6";
		}
	}
	
	@GetMapping({ "/reset", "/reset/{onlyone}" })
	public String resetEmpty(@PathVariable(required = false) String onlyone) {
		return "redirect:/?loginFailure=6";
	}
	
	@GetMapping("/reset/{email}/{reasonKey}")
	public String confirmReset(Model model, Principal principal, @PathVariable(required = true) String email, @PathVariable(required = true) String reasonKey) {
		Optional<User> user = userRepository.findByEmail(email);
		if (!user.isPresent() || !reasonKey.equals(user.get().getReason())) {
			return "redirect:/?loginFailure=6";
		}
		//user reset ok, email and reasonKey matching!
		model.addAttribute("user", user.get());
		return "reset-password";
	}
	
	// add request mapping for /access-denied
	@GetMapping("/access-denied")
	public String showAccessDenied() {
		return "access-denied";
	}

}