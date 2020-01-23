package com.gift_me_five.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;

@Embeddable
public class GiverSeeWishlistId implements Serializable {

	private static final long serialVersionUID = -430976122726951798L;
	private Long userId;
	private Long wishlistId;

	public GiverSeeWishlistId() {
	}

	public GiverSeeWishlistId(Long userId, Long wishlistId) {
		super();
		this.userId = userId;
		this.wishlistId = wishlistId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;

		if (o == null || getClass() != o.getClass())
			return false;

		GiverSeeWishlistId that = (GiverSeeWishlistId) o;
		return Objects.equals(userId, that.userId) && Objects.equals(wishlistId, that.wishlistId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(userId, wishlistId);
	}
}
