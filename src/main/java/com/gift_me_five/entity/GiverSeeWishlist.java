package com.gift_me_five.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

@Entity
public class GiverSeeWishlist {

	@EmbeddedId
	private GiverSeeWishlistId id;

	@ManyToOne
	@MapsId("userId")
	private User user;

	@ManyToOne
	@MapsId("wishlistId")
	private Wishlist wishlist;

	public GiverSeeWishlistId getId() {
		return id;
	}

	public void setId(GiverSeeWishlistId id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Wishlist getWishlist() {
		return wishlist;
	}

	public void setWishlist(Wishlist wishlist) {
		this.wishlist = wishlist;
	}
}
