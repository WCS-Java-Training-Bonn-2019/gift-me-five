package com.gift_me_five.controller;

import java.security.Principal;
import java.util.Optional;
import java.util.logging.Logger;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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
		GiftMeFive.debugOut(principal.getName());
		model.addAttribute("user", new User());

		return "registration-form";
	}
	
	@GetMapping("/showRegistrationForm")
	public String showMyRegistrationPage(Model model) {
		
		model.addAttribute("user", new User());

		return "registration-form";
	}

	@PostMapping("/processRegistrationForm")
	public String processRegistrationForm(@Valid @ModelAttribute("user") User theUser, BindingResult theBindingResult,
			Model theModel) {

		String newEmailLogin = theUser.getEmail();
		logger.info("Processing registration form for: " + newEmailLogin);

		// form validation
		if (theBindingResult.hasErrors()) {
			return "registration-form";
		}

		// check the database if user already exists
		Optional<User> existing = userRepository.findByEmail(newEmailLogin);
		if (existing.isPresent()) {
			theModel.addAttribute("user", new User());
			theModel.addAttribute("registrationError", "User name already exists.");

			if (!theUser.getPassword().equals(existing.get().getPassword())) {
				// password encrypten
				theUser.setPassword(passwordEncoder.encode(theUser.getPassword()));
			}
			logger.warning("User name already exists.");
			return "registration-form";

		} else {

			// password encrypten
			theUser.setPassword(passwordEncoder.encode(theUser.getPassword()));
		}

		// create user account

		userRepository.save(theUser);

		logger.info("Successfully created user: " + newEmailLogin);

		return "registration-confirmation";
	}

}
