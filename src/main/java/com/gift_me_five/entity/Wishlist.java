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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Wishlist {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String uniqueUrlGiver;
	private String uniqueUrlReceiver;
	
	private String title;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "createDate", updatable = false, nullable = true, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Date createDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modifyDate", updatable = false, nullable = true, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	private Date modifyDate;

	@ManyToOne
	@JoinColumn(name = "receiverId", nullable = false)
	private User receiver;

	@ManyToOne
	@JoinColumn(name = "themeId")
	private Theme theme;

	@ManyToMany
	@JoinTable(name = "GiverSeeWishlist", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "wishlist_id"))
	private List<User> givers = new ArrayList<>();

	@OneToMany(mappedBy = "wishlist", cascade = CascadeType.REMOVE)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private List<Wish> wishes = new ArrayList<>();

	// wishlist in join table 
	@OneToMany(mappedBy = "wishlist", cascade = CascadeType.REMOVE)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private List<GiverSeeWishlist> giverSeeWishlists = new ArrayList<>();

	public Wishlist() {
	}
}
