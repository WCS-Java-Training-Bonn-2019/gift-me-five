package com.gift_me_five.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String login;

	@Column(nullable = false)
	private String password;

	private String firstname;
	private String lastname;
	private String email;

	private Long failedLogins = 0L;

	// admin or user
	// todo default value "" or null?!
	private String role;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "lastLogin", updatable = false, nullable = true, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Date lastLogin;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "createDate", updatable = false, nullable = true, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Date createDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modifyDate", updatable = false, nullable = true, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	private Date modifyDate;

	// wishlists that use receiverId
	// wishlist become obsolete if they have no receiver -> cascase.remove
	@OneToMany(mappedBy = "receiver", cascade = CascadeType.REMOVE)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private List<Wishlist> wishlists = new ArrayList<>();

	// wishes that use userId as giver
	// wishes needs giverId set to null
	@OneToMany(mappedBy = "giver")
	private List<Wish> wishes = new ArrayList<>();

	// wishes needs giverId set to null if user is removed
	@PreRemove
	private void removeGiverFromWish() {
		for (Wish child : this.wishes) {
			child.setGiver(null);
		}
	}

	// join table user<->wishlist with userId
	// if user is removed, remove all giverId in join for this user
	@OneToMany(mappedBy = "wishlist", cascade = CascadeType.REMOVE)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private List<GiverSeeWishlist> giverSeeWishLists = new ArrayList<>();

	public User() {
	}
	
}
