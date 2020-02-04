package com.gift_me_five.config;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class GiftMeFiveSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		auth.inMemoryAuthentication()
		.withUser("michaela").password(encoder.encode("mi")).roles("USER")
		.and()
		.withUser("alfred").password(encoder.encode("al")).roles("USER")
		.and()
		.withUser("admin").password(encoder.encode("ad")).roles("ADMIN");
	}
	@Override
	protected void configure(HttpSecurity http) throws Exception {
	    http
	        .authorizeRequests()
	          .antMatchers("/admin/**").hasRole("ADMIN")
	          //.anyRequest().authenticated()
	          .and()
	        .formLogin()
	        	.loginPage("/showMyLoginPage")
	        	.loginProcessingUrl("/authenticateTheUser")
				.permitAll()
			.and()
				.logout().permitAll()
				.logoutSuccessUrl("/")
				.and()
				.exceptionHandling().accessDeniedPage("/access-denied");
	      //     .and()
	      //  .httpBasic();
	}
}