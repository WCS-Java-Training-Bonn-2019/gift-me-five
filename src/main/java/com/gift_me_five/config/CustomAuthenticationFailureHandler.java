package com.gift_me_five.config;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomAuthenticationFailureHandler 
implements AuthenticationFailureHandler {

  private ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void onAuthenticationFailure(
    HttpServletRequest request,
    HttpServletResponse response,
    AuthenticationException exception) 
    throws IOException, ServletException {

	  String loginFailureUser= request.getParameter("username");
	  
	  // ToDo  loginFailure  failed_logins fuer DB einbauen
	  System.out.println("\n\n*************** hier der User der Mist baut: " + loginFailureUser + "******************\n\n");
	  
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      
//      Map<String, Object> data = new HashMap<>();
//      data.put(
//        "timestamp", 
//        Calendar.getInstance().getTime());
//      data.put(
//        "exception", 
//        exception.getMessage());
//      data.put("Hier der User der Mist baut: ", loginFailureUser);
      
//      response.getOutputStream()
//        .println(objectMapper.writeValueAsString(data));
      
         
      response.sendRedirect("/?loginFailure=1");
      
    }
 
  /*
   * implements AuthenticationFailureHandler {

  private ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void onAuthenticationFailure(
    HttpServletRequest request,
    HttpServletResponse response,
    AuthenticationException exception) 
    throws IOException, ServletException {

	  String loginFailureUser= request.getParameter("username");
	  
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      
      Map<String, Object> data = new HashMap<>();
      data.put(
        "timestamp", 
        Calendar.getInstance().getTime());
      data.put(
        "exception", 
        exception.getMessage());
      data.put("Hier der User der Mist baut: ", loginFailureUser);

      response.getOutputStream()
        .println(objectMapper.writeValueAsString(data));
      
    }
  
}
   * 
   */

}