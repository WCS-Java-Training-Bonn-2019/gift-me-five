package com.gift_me_five.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class GiverSeeWishlist {

	@EmbeddedId
	private GiverSeeWishlistId id;

	@ManyToOne
	@MapsId("userId")
	private User user;

	@ManyToOne
	@MapsId("wishlistId")
	private Wishlist wishlist;
	
	public GiverSeeWishlist() {
	}
}
