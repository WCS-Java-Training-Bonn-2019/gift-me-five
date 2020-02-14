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

	public Wish ownWish(Long id) {
		// Returns the wish specified by id if the current user is the receiver of this
		// wish
		// Returns null otherwise.
		User currentUser = getCurrentUser();
		if (currentUser != null) {
			Optional<Wish> optionalWish = wishRepository.findById(id);
			if (optionalWish.isPresent() && optionalWish.get().getWishlist().getReceiver().equals(currentUser)) {
				return optionalWish.get();
			}
		}
		return null;
	}

	public Wish friendWish(Long id) {
		// Returns the wish specified by id if the current user is registered as giver
		// for the
		// wishlist of this wish.
		// Returns null otherwise.

		Optional<Wish> optionalWish = wishRepository.findById(id);
		if (optionalWish.isPresent() && friendWishlist(optionalWish.get().getWishlist().getId()) != null) {
			return optionalWish.get();
		}
		return null;
	}

	public Wish publicWishReceiver(Long id, String uniqueUrlReceiver) {
		if (uniqueUrlReceiver != null) {
			Optional<Wish> optionalWish = wishRepository.findById(id);
			if (optionalWish.isPresent()
					&& uniqueUrlReceiver.equals(optionalWish.get().getWishlist().getUniqueUrlReceiver())) {
				return optionalWish.get();
			}
		}
		return null;
	}
	
	//*************************************************************************************
	//TODO:
	//publicWishGiver can also be retrieved with uniqueUrlReceiver
	//Reason: images for wishes must be displayed on Giver and Receiver wishlists
	//Is this a problem?
	//*************************************************************************************
	
	public Wish publicWishGiver(Long id, String uniqueUrl) {
		// TODO Auto-generated method stub
		if (uniqueUrl != null) {
			Optional<Wish> optionalWish = wishRepository.findById(id);
			if (optionalWish.isPresent()
					&& (uniqueUrl.equals(optionalWish.get().getWishlist().getUniqueUrlGiver()))
					|| uniqueUrl.equals(optionalWish.get().getWishlist().getUniqueUrlReceiver())) {
				return optionalWish.get();
			}
		}
		return null;
	}

	public List<Wish> unSelectedWishes(Wishlist wishlist, boolean sort) {
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

	public Wishlist ownWishlist(Long id) {

		// Returns the wishlist specified by id if the current user is the receiver of
		// this wishlist
		// Returns null otherwise.

		User currentUser = getCurrentUser();
		if (currentUser != null) {
			List<Wishlist> listWishlist = wishlistRepository.findByIdAndReceiver(id, currentUser);
			if (!listWishlist.isEmpty()) {
				return listWishlist.get(0);
			}
		}
		return null;
	}

	public Wishlist friendWishlist(Long id) {
		// Returns the wishlist specified by id if the current user is one of the givers
		// of this wishlist
		// Returns null otherwise.

		User currentUser = getCurrentUser();
		if (currentUser != null) {
			List<Wishlist> listWishlist = wishlistRepository.findByIdAndGivers(id, currentUser);
			if (!listWishlist.isEmpty()) {
				return listWishlist.get(0);
			}
		}
		return null;
	}

	public Wishlist publicWishlist(String uniqueUrlReceiver) {
		// Returns the wishlist specified by uniqueUrlReceiver if it is a public
		// wishlist
		// Returns null otherwise.
		// Assumes the public user exists and has Id 2!

		if (uniqueUrlReceiver != null) {
			Wishlist wishlist = wishlistRepository.findByUniqueUrlReceiver(uniqueUrlReceiver);
			if (wishlist != null && wishlist.getReceiver().getId() == 2L) {
				return wishlist;
			}
		}
		return null;
	}

	public List<Wishlist> allOwnWishlists() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			return wishlistRepository.findByReceiverEmail(authentication.getName());
		}
		return null;
	}

	public List<Wishlist> allFriendWishlists() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			return wishlistRepository.findByGiversEmail(authentication.getName());
		}
		return null;
	}

}
