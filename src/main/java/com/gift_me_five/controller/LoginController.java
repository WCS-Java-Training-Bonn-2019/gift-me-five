package com.gift_me_five.controller;

import java.security.Principal;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

	private Optional<String> readCookie(HttpServletRequest request, String key) {
		return Arrays.stream(request.getCookies()).filter(c -> key.equals(c.getName())).map(Cookie::getValue).findAny();
	}

	@GetMapping("/")
	public String showStartPage(Model model, Principal principal, HttpServletRequest request,
			HttpServletResponse response, @RequestParam(required = false) Long loginFailure) {

		if (principal != null) {
			// DEBUG ONLY **************************************************************
//			System.out.println("*".repeat(40));
//			System.out.println("Anzahl Cookies: " + request.getCookies().length);
//			for (Cookie cookie : request.getCookies()) {
//				System.out.println(cookie.getName() + " = " + cookie.getValue());
//			}
//			System.out.println("*".repeat(40));
			// **************************************************************************
			Optional<String> optionalInviteUuid = readCookie(request, "invite");
			if (optionalInviteUuid.isPresent()) {
				Cookie removeCookie = new Cookie("invite", "");
				removeCookie.setMaxAge(0);
				response.addCookie(removeCookie);
				return "redirect:/public/wishlist/accept/" + optionalInviteUuid.get();
			}
		}

		model.addAttribute("myWishlists", userArtifactsService.getAllMyWishlistsAsReceiver());
		model.addAttribute("friendWishlists", userArtifactsService.getAllMyWishlistsAsGiver());
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
			simpleEmailSerive.tryToSendEmail(existing.getEmail(), "Reset PW", "http://" + request.getLocalName() + ":"
					+ request.getLocalPort() + "/reset/" + existing.getEmail() + "/" + existing.getReason() + "/");

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

			Authentication authentication = new UsernamePasswordAuthenticationToken(confirmedUser,
					confirmedUser.getPassword(), confirmedUser.getAuthorities());
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
	public String confirmReset(Model model, Principal principal, @PathVariable(required = true) String email,
			@PathVariable(required = true) String reasonKey) {
		Optional<User> user = userRepository.findByEmail(email);
		if (!user.isPresent() || !reasonKey.equals(user.get().getReason())) {
			return "redirect:/?loginFailure=6";
		}
		// user reset ok, email and reasonKey matching!
		model.addAttribute("user", user.get());
		return "reset-password";
	}

	// add request mapping for /access-denied
	@GetMapping("/access-denied")
	public String showAccessDenied() {
		return "access-denied";
	}

}