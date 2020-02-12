package com.gift_me_five.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@EnableWebSecurity
public class GiftMeFiveSecurityConfig extends WebSecurityConfigurerAdapter {
	
	private final UserDetailsService userDetailsService; 
	
	private final PasswordEncoder passwordEncoder;
	
	@Autowired
	public GiftMeFiveSecurityConfig(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
		this.userDetailsService = userDetailsService;
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//		PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
//		auth.inMemoryAuthentication()
//		.withUser("michaela").password(encoder.encode("m")).roles("USER")
//		.and()
//		.withUser("alfred").password(encoder.encode("a")).roles("USER")
//		.and()
//		.withUser("admin").password(encoder.encode("a")).roles("ADMIN");
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
	    http
	        .authorizeRequests()
	        	.antMatchers("/", "/css/**", "/pics/**", "/newwishlist/**", "/showRegistrationForm/**", "/processRegistrationForm/**" , "/under_construction/**", "/wishlist", "/confirm/**", "/forgot_password/**", "/reset/**").permitAll()
	        	.antMatchers("/admin/**").hasRole("admin")
	        	.anyRequest().authenticated()
	        	.and()
	        	.formLogin()
	        	//.failureUrl("/?loginFailure=1")
	        	.failureHandler(customAuthenticationFailureHandler())
	        	.successHandler(customAuthenticationSuccessHandler())
	        	.loginPage("/")
	        	.loginProcessingUrl("/authenticateTheUser")
	        	.permitAll()
	        	.and()
	        	.logout().permitAll()
	        	.logoutSuccessUrl("/")
	        	.and()
	        	.rememberMe()
	        	.and()
	        	.exceptionHandling().accessDeniedPage("/access-denied");
	      //     .and()
	      //  .httpBasic();
	}

	  @Bean
	    public AuthenticationFailureHandler customAuthenticationFailureHandler() {
	        return new CustomAuthenticationFailureHandler();
	    }
	  
	  @Bean
	  public AuthenticationSuccessHandler customAuthenticationSuccessHandler(){
		  return new CustomAuthenticationSuccessHandler();
	  }
}