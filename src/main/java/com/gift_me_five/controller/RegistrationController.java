package com.gift_me_five.controller;

import java.security.Principal;
import java.util.Optional;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gift_me_five.GiftMeFive;
import com.gift_me_five.entity.User;
import com.gift_me_five.repository.UserRepository;

@Controller
public class RegistrationController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private Logger logger = Logger.getLogger(getClass().getName());

	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {

		StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);

		dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);

	}

	@GetMapping("/profile")
	public String editProfile(Model model, Principal principal) {
		// todo get user credential via principal
		Optional<User> optionalUser = userRepository.findByEmail(principal.getName());
		if (optionalUser.isPresent()) {
			model.addAttribute("user", optionalUser.get());
		}

		return "registration-form";
	}

	@GetMapping("/showRegistrationForm")
	public String showMyRegistrationPage(Model model, @RequestParam(required = false) Long loginFailure) {

		model.addAttribute("user", new User());
		model.addAttribute("loginFailure", loginFailure);
		return "registration-form";
	}

	@PostMapping("/processRegistrationForm")
	public String processRegistrationForm(@Valid @ModelAttribute("user") User theUser, BindingResult theBindingResult,
			Model theModel, Principal principal, HttpServletRequest request) {

		String newEmailLogin = theUser.getEmail();
		logger.info("Processing registration form for: " + newEmailLogin);

		// form validation
		if (theBindingResult.hasErrors()) {
			return "registration-form";
		}

		// check the database if user already exists
		// and no principal, because edit WILL perform on existing account
		// must use findById if possible because email might have changed
		Optional<User> existing;
		if (theUser.getId() == null) {
			existing = userRepository.findByEmail(newEmailLogin);
		} else {
			existing = userRepository.findById(theUser.getId());
		}
		if (existing.isPresent() && principal == null) {
			theModel.addAttribute("user", new User());
			theModel.addAttribute("registrationError", "User name already exists.");
			logger.warning("User name already exists.");
			return "redirect:/showRegistrationForm/?loginFailure=3";

		}

		// password check and encode must be done for new and existing user (edit
		// profile)
		if (!existing.isPresent() || !theUser.getPassword().equals(existing.get().getPassword())) {
			// password encrypten
			theUser.setPassword(passwordEncoder.encode(theUser.getPassword()));
		}

		// create user account

		userRepository.save(theUser);

		// user has changed his email
		// user is logged in (still old email)
		// findByEmail does NOT get a result (isEmpty)
		// findById is still working (Not isEmpty)
		if (principal != null && userRepository.findByEmail(principal.getName()).isEmpty()
				&& !userRepository.findById(theUser.getId()).isEmpty()) {

			// change credentials without logout
			Authentication authentication = new UsernamePasswordAuthenticationToken(theUser, theUser.getPassword(),
					theUser.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);

			return "redirect:/";
		}

		logger.info("Successfully created/edited user: " + newEmailLogin);

		return "registration-confirmation";
	}

	@GetMapping("/delete_profile")
	public String deleteUser(Principal principal, HttpServletRequest request) {
		// delete user in DB
		if (principal != null) {
			User user = userRepository.findByEmail(principal.getName()).get();
			userRepository.deleteById(user.getId());
		}

		// terminate session -> force logout
		request.getSession().invalidate();
		return "redirect:/";
	}

}
