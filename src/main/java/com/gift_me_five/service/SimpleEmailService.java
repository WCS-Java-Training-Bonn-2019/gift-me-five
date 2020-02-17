package com.gift_me_five.service;

import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.gift_me_five.entity.Wishlist;

@Service
public class SimpleEmailService {

	@Autowired
	private JavaMailSender sender;

	@Autowired
	private UserArtifactsService userArtifactsService;
	
    @Value(value = "${location.name}")
	private String locationName;
	
    @Value(value = "${location.port}")	
	private String locationPort;

	public void email(String receiver, String subject, String message) {
		try {
			sendEmail(receiver, subject, message);
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage());
		}
	}

	private void sendEmail(String receiver, String subject, String message) throws Exception {
		MimeMessage mail = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mail);

		helper.setTo(receiver);
		helper.setText(message);
		helper.setSubject(subject);

		sender.send(mail);
	}

	public String emailDummy(String receiver, String subject, String message) {

		String emailText = "\n\n" + "*".repeat(50) + "\n" + "SendTo: |" + receiver + "|\n" + "Subject: " + subject
				+ "\n" + "Message: \n" + message + "\n" + "*".repeat(50) + "\n\n";
		System.out.println(emailText);
		return emailText;
	}

	public boolean emailAddressFormatCheck(String emailAddress) {

		String[] emailComponents = emailAddress.split("@");
		if (emailComponents.length != 2) {
			// Must contain exactly one @ character
			System.out.println("Too many @ or name or domain missing");
			return false;
		}
		if (!emailComponents[0].matches("\\w[\\w\\.\\_\\-]*")) { //
			// Component before @ must start with word character and contain letters,
			// digits, '.', '_', '-' (to be expanded if needed)
			System.out.println("/" + emailComponents[0] + "/ : Name format wrong ");
			return false;
		}
		String[] domainComponents = emailComponents[1].split("\\.");
		if (domainComponents.length < 2) {
			// Must contain at least one . character
			System.out.println("/" + emailComponents[1] + "/ : Top Level Domain not specified");
			return false;
		}
		if (!domainComponents[domainComponents.length - 1].matches("[A-Za-z]+")) {
			// TLD only letters and at least one character
			System.out
					.println("/" + domainComponents[domainComponents.length - 1] + "/ : Top Level Domain format wrong");
			return false;
		}
		for (String domainComponent : domainComponents) {
			if (!domainComponent.matches("[\\w\\-_]+")) {
				// domain components only contain letters, numbers, - and _
				System.out.println("/" + domainComponent + "/ : Domain component format wrong");
				return false;
			}
		}
		// ...
		// further checks to be added!
		return true;
	}
	
	/**
	 * Sends out an invitation mail to become giver for a specified wishlist to a list of of recipients.
	 * No email will be sent if any of the addresses is malformed.  
	 * @param wishlist the wishlist for which the recipients shall be invited as givers
	 * @param recipientList Comma or semicolon separated list of email addresses
	 * @return the list of recipientList components that could not be interpreted as valid email addresses (may be empty)
	 */
	public List<String> sendInviteEmails(Wishlist wishlist, String recipientList) {

		String[] giversEmails = recipientList.strip().split("[,;]");
		List<String> malformedEmails = new ArrayList<>();
		for (String email : giversEmails) {
			email = email.strip();
			if (!emailAddressFormatCheck(email)) {
				malformedEmails.add(email);
			}
		}

		if (malformedEmails.size() == 0) {
			// Send out emails to givers
			String uuid = wishlist.getUniqueUrlGiver();
			String subject = "Please check out my wishlist!";
			String messageBody = "Hi,\n" + "I'm " + userArtifactsService.getCurrentUser().getFirstname()
					+ " and I would like to invite you to my new wishlist: " + wishlist.getTitle() + ". \n\n"
					+ "http://" + this.locationName + ":" + this.locationPort + "/public/wishlist/invite/" + uuid
					+ "/";
            
			for (String email : giversEmails) {
				try {
					// Can be changed to 'real' email()
					emailDummy(email, subject, messageBody);
				} catch (Exception ex) {
					System.out.println("Error in sending email: " + ex);
				}
			}
		}
		
		return malformedEmails;

	}
}