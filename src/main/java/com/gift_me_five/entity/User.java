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

@Entity
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public Long getFailedLogins() {
		return failedLogins;
	}

	public void setFailedLogins(Long failedLogins) {
		this.failedLogins = failedLogins;
	}

	public List<Wishlist> getWishlists() {
		return wishlists;
	}

	public void setWishlists(List<Wishlist> wishlists) {
		this.wishlists = wishlists;
	}

	public List<Wish> getWishes() {
		return wishes;
	}

	public void setWishes(List<Wish> wishes) {
		this.wishes = wishes;
	}

	public List<GiverSeeWishlist> getGiverSeeWishLists() {
		return giverSeeWishLists;
	}

	public void setGiverSeeWishLists(List<GiverSeeWishlist> giverSeeWishLists) {
		this.giverSeeWishLists = giverSeeWishLists;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

}
