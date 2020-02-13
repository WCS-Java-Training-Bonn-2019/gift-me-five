package com.gift_me_five.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gift_me_five.entity.User;
import com.gift_me_five.entity.Wishlist;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

	Optional<Wishlist> findFirstByReceiver(User user);

	List<Wishlist> findByReceiver(User user);

	List<Wishlist> findByGivers(User currentUser);

	List<Wishlist> findByIdAndGivers(Long id, User currentUser);

	List<Wishlist> findByIdAndReceiver(Long id, User currentUser);

	Wishlist findFirstByIdGreaterThan(long l);

	List<Wishlist> findByGiversEmail(String email);

	List<Wishlist> findByReceiverEmail(String email);

	Wishlist findByUniqueUrlReceiver(String uniqueUrlReceiver);

	Optional<Wishlist> findByUniqueUrlGiver(String uniqueUrlGiver);
}
