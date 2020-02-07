package com.gift_me_five.config;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.gift_me_five.GiftMeFive;
import com.gift_me_five.entity.User;
import com.gift_me_five.repository.UserRepository;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

	@Autowired
	private UserRepository userRepository;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {

		GiftMeFive.debugOut(exception.getMessage());
				
		// is user locked ?
		if (exception.getMessage().equals("User account is locked")) {
			
			GiftMeFive.debugOut("User is lockded");
			
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			
			response.sendRedirect("/?loginFailure=2");

		} else {

			// get the user from Http-Request --Yeah--
			String loginFailureUser = request.getParameter("username");

			// check the database if user already exists
			Optional<User> existing = userRepository.findByEmail(loginFailureUser);
			if (existing.isPresent()) {

				// get number of failed login from db
				Long countFailedLogin = existing.get().getFailedLogins();

				countFailedLogin += 1;

				// set new number for failed_login in db
				existing.get().setFailedLogins(countFailedLogin);
				userRepository.save(existing.get());
				
				response.setStatus(HttpStatus.UNAUTHORIZED.value());

				response.sendRedirect("/?loginFailure=1");

			} else {
				// user not in db
				response.setStatus(HttpStatus.UNAUTHORIZED.value());

				response.sendRedirect("/?loginFailure=1");
			}
		}

		

	}

}