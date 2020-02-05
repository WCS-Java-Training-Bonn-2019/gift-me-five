package com.gift_me_five.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.gift_me_five.entity.User;
import com.gift_me_five.entity.Wishlist;
import com.gift_me_five.repository.UserRepository;
import com.gift_me_five.repository.WishlistRepository;

@Service
public class UserArtifactsService {

	@Autowired
	private UserRepository userRepository;

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

	public Wishlist ownWishlist(Long id) {
		
		// Returns the wishlist specified by id if the current user is the receiver of this wishlist
		// Returns null otherwise.

		User currentUser = getCurrentUser();
		List<Wishlist> listWishlist = wishlistRepository.findByIdAndReceiver(id, currentUser);
		if (listWishlist.isEmpty()) {
			return null;
		} else {
			return listWishlist.get(0);
		}		
	}
	
	public Wishlist friendWishlist(Long id) {
		// Returns the wishlist specified by id if the current user is one of the givers of this wishlist
		// Returns null otherwise.

		User currentUser = getCurrentUser();
		List<Wishlist> listWishlist = wishlistRepository.findByIdAndGivers(id, currentUser);
		if (listWishlist.isEmpty()) {
			return null;
		} else {
			return listWishlist.get(0);
		}		
	}

	public List<Wishlist> allOwnWishlists() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return wishlistRepository.findByReceiverEmail(authentication.getName());
	}

	public List<Wishlist> allFriendWishlists() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return wishlistRepository.findByGiversEmail(authentication.getName());
	}

}
