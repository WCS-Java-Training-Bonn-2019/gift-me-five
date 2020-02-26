package com.gift_me_five.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.gift_me_five.entity.User;
import com.gift_me_five.entity.Wish;
import com.gift_me_five.entity.Wishlist;
import com.gift_me_five.repository.UserRepository;
import com.gift_me_five.repository.WishRepository;
import com.gift_me_five.repository.WishlistRepository;

@Service
public class UserArtifactsService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private WishRepository wishRepository;

	@Autowired
	private WishlistRepository wishlistRepository;

	public User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			String currentUserName = authentication.getName();
			Optional<User> optionalUser = userRepository.findByEmail(currentUserName);
			if (!optionalUser.isPresent()) {
				throw new UsernameNotFoundException("No user found for this principal!!!");
			}
			return optionalUser.get();
		}
		return null;
	}
	
	public User getPublicUser() {
		Optional<User> optionalUser = userRepository.findById(2L);
		if (!optionalUser.isPresent()) {
			throw new UsernameNotFoundException("No public user found !!!");
		}
		return optionalUser.get();
	}
	
	/**
	 * 
	 * @param wishId
	 * @return the wish specified by wishId if the current user is the receiver of this wish, otherwise null.
	 */

	public Wish getWishIfReceiver(Long wishId) {

		User currentUser = getCurrentUser();
		if (currentUser != null) {
			Optional<Wish> optionalWish = wishRepository.findById(wishId);
			if (optionalWish.isPresent() && ( optionalWish.get().getWishlist().getReceiver().equals(currentUser) || "admin".equals(currentUser.getEmail()))) {
				return optionalWish.get();
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param wishId
	 * @return the wish specified by wishId if the current user is registered as giver for the wish's wishlist, otherwise null
	 */

	public Wish getWishIfGiver(Long wishId) {

		Optional<Wish> optionalWish = wishRepository.findById(wishId);
		if (optionalWish.isPresent() && (optionalWish.get().getWishlist().getReceiver().getId() == 2 || getWishlistIfGiver(optionalWish.get().getWishlist().getId()) != null)) {
			return optionalWish.get();
		}
		return null;
	}

	public Wish getPublicWishForReceiver(Long id, String uniqueUrlReceiver) {
		if (uniqueUrlReceiver != null) {
			Optional<Wish> optionalWish = wishRepository.findById(id);
			if (optionalWish.isPresent()
					&& uniqueUrlReceiver.equals(optionalWish.get().getWishlist().getUniqueUrlReceiver())) {
				return optionalWish.get();
			}
		}
		return null;
	}
	
	
	public Wish getPublicWishForGiver(Long id, String uniqueUrlGiver) {
		// TODO Auto-generated method stub
		if (uniqueUrlGiver != null) {
			Optional<Wish> optionalWish = wishRepository.findById(id);
			if (optionalWish.isPresent()
					&& uniqueUrlGiver.equals(optionalWish.get().getWishlist().getUniqueUrlGiver())) {
				return optionalWish.get();
			}
		}
		return null;
	}
	
	

	public List<Wish> listUnSelectedWishesForGiver(Wishlist wishlist, boolean sort) {
		// Returns a list of all wishes on a wishlist, sorted so that
		// all wishes with current user as giver are at the beginning.
		// TODO:
		// What if there is no authentication?

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		List<Wish> wishes = new ArrayList<>();
		List<Wish> unselectedWishes = new ArrayList<>();
		List<Wish> unorderedWishes;
		if (sort) {
			unorderedWishes = wishRepository.findByWishlistOrderByPrice(wishlist);
		} else {
			unorderedWishes = wishlist.getWishes();
		}
		for (Wish wish : unorderedWishes) {
			if (authentication != null) {
				// For authenticated users, self-selected wishes are sorted to the top
				if (wish.getGiver() != null && authentication.getName().equals(wish.getGiver().getEmail())) {
					wishes.add(wish);
				} else {
					unselectedWishes.add(wish);
				}
			} else {
				// For unauthenticated users, all selected wishes are sorted to the bottom
				// If this is not desired, simply replace the next if{}else{} by wishes.add(wish).
				if (wish.getGiver() == null) {
					wishes.add(wish);
				} else {
					unselectedWishes.add(wish);
				}
			}
		}
		wishes.addAll(unselectedWishes);
		return wishes;
	}
	
	/**
	 * 
	 * @param wishlistId
	 * @return the wishlist specified by wishlistId if the current user is the receiver of this wishlist, otherwise null.
	 */

	public Wishlist getWishlistIfReceiver(Long wishlistId) {

		User currentUser = getCurrentUser();
		if (currentUser != null) {
			List<Wishlist> listWishlist = wishlistRepository.findByIdAndReceiver(wishlistId, currentUser);
			if (!listWishlist.isEmpty()) {
				return listWishlist.get(0);
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param wishlistId
	 * @return the wishlist specified by wishlistId if the current user is one of the givers of this wishlist, otherwise null
	 */

	public Wishlist getWishlistIfGiver(Long wishlistId) {

		User currentUser = getCurrentUser();
		if (currentUser != null) {
			List<Wishlist> listWishlist = wishlistRepository.findByIdAndGivers(wishlistId, currentUser);
			if (!listWishlist.isEmpty()) {
				return listWishlist.get(0);
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param uniqueUrlReceiver
	 * @return the wishlist specified by the receiver uuid uniqueUrlReceiver if it is a public wishlist (User Id==2L), otherwise null
	 */

	public Wishlist getWishlistIfPublicReceiver(String uniqueUrlReceiver) {

		if (uniqueUrlReceiver != null && wishlistRepository.findByUniqueUrlReceiver(uniqueUrlReceiver).isPresent()) {
			Wishlist wishlist = wishlistRepository.findByUniqueUrlReceiver(uniqueUrlReceiver).get();
			if (wishlist != null && wishlist.getReceiver().getId() == 2L) {
				return wishlist;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param uniqueUrlGiver
	 * @return the wishlist specified by the giver uuid uniqueUrlGiver if it is a public wishlist (User Id==2L), otherwise null
	 */

	public Wishlist getWishlistIfPublicGiver(String uniqueUrlGiver) {

		if (uniqueUrlGiver != null && wishlistRepository.findByUniqueUrlGiver(uniqueUrlGiver).isPresent()) {
			Wishlist wishlist = wishlistRepository.findByUniqueUrlGiver(uniqueUrlGiver).get();
			if (wishlist != null && wishlist.getReceiver().getId() == 2L) {
				return wishlist;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @return all wishlists where the authenticated user is registered as receiver (may be empty); if not authenticated, return null.
	 */

	public List<Wishlist> getAllMyWishlistsAsReceiver() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			return wishlistRepository.findByReceiverEmail(authentication.getName());
		}
		return null;
	}
	
	/**
	 * 
	 * @return all wishlists where the authenticated user is registered as giver (may be empty); if not authenticated, return null.
	 */

	public List<Wishlist> getAllMyWishlistsAsGiver() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			return wishlistRepository.findByGiversEmail(authentication.getName());
		}
		return null;
	}
	
	/**
	 * Will deliver a picture from the wish table in the DB for logged-in givers and receivers
	 * or for givers and receivers authenticated by the appropriate UUID.
	 * 
	 * @param wishId The id of the wish
	 * @param uniqueUrl UUID for unregistered wish receiver or giver; can be null
	 * @return the picture stored for a wish if the requester is allowed to see it; null otherwise
	 */
	public byte[] fetchWishPicture(Long wishId, String uniqueUrl) {
		
		Wish wish;
		if (uniqueUrl == null) {
			wish = this.getWishIfReceiver(wishId);
			if (wish == null) wish = this.getWishIfGiver(wishId);
		} else {
			wish = this.getPublicWishForReceiver(wishId, uniqueUrl);
			if (wish == null) wish = this.getPublicWishForGiver(wishId, uniqueUrl);
		}
		if (wish != null) return wish.getPicture();

		return null;
	}

}
