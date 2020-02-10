package com.gift_me_five.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class User implements UserDetails {

	private static final long serialVersionUID = 7089309836794614341L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String email;

//	no more login, use email
//	@Column(nullable = false, unique = true)
//	private String login;

	@Column(nullable = false)
	private String password;

	private String firstname;
	private String lastname;

	private Long failedLogins;

	// admin or user
	private String role;

	// unique key for user mail handling - confirmation, pw reset
	private String reason;
	
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
		this.failedLogins = 0L; // default: no failedLogins at registration
		this.role = "pending";  // default: new users are pending
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
		return Collections.singletonList(authority);
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO locked if failedLogins > 3
		if (this.getFailedLogins() > 3) {
			return false;
		} else
			return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO role == registered
		return true;
	}
}
