package com.gift_me_five.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gift_me_five.entity.User;
import com.gift_me_five.entity.Wishlist;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
	
	Wishlist findFirstByReceiver(User user);
	Wishlist findFirstByIdGreaterThan(long id);
}
