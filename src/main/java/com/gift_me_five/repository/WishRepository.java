package com.gift_me_five.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gift_me_five.entity.Wish;
import com.gift_me_five.entity.Wishlist;

@Repository
public interface WishRepository extends JpaRepository<Wish, Long> {

	List<Wish> findById(long l);
	List<Wish> findByIdLessThan(long l);
	List<Wish> findByWishlist(Wishlist l);

}
