package com.gift_me_five;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class GiftMeFive {

	public static void main(String[] args) {
		SpringApplication.run(GiftMeFive.class, args);

	}

	//redirect http -> https
    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };
        tomcat.addAdditionalTomcatConnectors(redirectConnector());
        return tomcat;
    }

    private Connector redirectConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setPort(8080);
        connector.setSecure(false);
        connector.setRedirectPort(8443);
        return connector;
    }
    
    
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	/**
	 * Outputs a message to console (*** message ***) and waits for timeout seconds
	 * @param message for output
	 * @param timeout in seconds (optional)
	 */
	public static void debugOut(Object message) {
		System.out.println();
		System.out.println();
		System.out.println("*".repeat(120));
		System.out.println(message);
		System.out.println("*".repeat(120));
		System.out.println();
		System.out.println();
	}

	/**
	 * Outputs a message to console (*** message ***) and waits for timeout seconds
	 * @param message for output
	 * @param timeout in seconds (optional)
	 */
	public static void debugOut(Object message, int timeout) {
		System.out.println();
		System.out.println();
		System.out.println("*".repeat(120));
		System.out.println(message);
		System.out.println("*".repeat(120));
		System.out.println();
		System.out.println();

		try {
			Thread.sleep(timeout * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
