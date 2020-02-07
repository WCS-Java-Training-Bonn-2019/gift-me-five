package com.gift_me_five.config;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.gift_me_five.entity.User;
import com.gift_me_five.repository.UserRepository;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	@Autowired
	private UserRepository userRepository;

	private final Long resetFailedLogin = 0L;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		// get the user from Http-Request --Yeah--
		String loginSuccesseUser = request.getParameter("username");

		// check the database if user already exists
		Optional<User> existing = userRepository.findByEmail(loginSuccesseUser);
		if (existing.isPresent()) {

			// reset failed_login in db
			existing.get().setFailedLogins(resetFailedLogin);
			
			// save user in db
			userRepository.save(existing.get());

			response.setStatus(HttpStatus.OK.value());

			response.sendRedirect("/");
		}

	}

}