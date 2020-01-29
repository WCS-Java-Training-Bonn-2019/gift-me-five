package com.gift_me_five;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gift_me_five.entity.Theme;
import com.gift_me_five.entity.User;
import com.gift_me_five.entity.Wish;
import com.gift_me_five.entity.Wishlist;
import com.gift_me_five.repository.ThemeRepository;
import com.gift_me_five.repository.UserRepository;
import com.gift_me_five.repository.WishRepository;
import com.gift_me_five.repository.WishlistRepository;

@Configuration
public class GiftMeFiveConfig {

	@Bean
	public CommandLineRunner dbInit(UserRepository userRepository, ThemeRepository themeRepository,
			WishlistRepository wishlistRepository, WishRepository wishRepository) {
		return (args) -> {
			emptyWishlistTable(wishlistRepository);
			emptyUserTable(userRepository);
			emptyThemeTable(themeRepository);
			final String[] userNames = { "publicDummyUser", "Michaela", "Frieda", "Alfred" };
			final String[] passwords = { "jykQGKpb;q-9FjkX8r_IB", "#1michaelasSecretPassword",
					"#1friedasSecretPassword", "#1alfredsSecretPassword" };
			final String[] backgroundPictures = { "white-4742365_640.jpg", "white-4742365_640.jpg", "postcard-1529121_640.png", "christmas-2947257_640.jpg" };
			for (int i = 0; i < userNames.length; i++) {
				Theme theme = createTheme(themeRepository, backgroundPictures[i]);
				User receiver = createUser(userRepository, userNames[i], passwords[i]);
				Wishlist wishlist = createWishlist(receiver, theme, wishlistRepository);
				createWishes(wishRepository, wishlist);

			}
		};
	}

	private User createUser(UserRepository userRepository, String userName, String password) {
		User user = new User();
		user.setLogin(userName);
		user.setPassword(password);
		userRepository.save(user);
		return user;
	}

	private Theme createTheme(ThemeRepository themeRepository, String picture) {
		Theme theme = new Theme();
		theme.setBackgroundPicture(picture);
		themeRepository.save(theme);
		return theme;
	}

	private Wishlist createWishlist(User receiver, Theme theme, WishlistRepository wishlistRepository) {
		Wishlist wishlist = new Wishlist();
		wishlist.setTheme(theme);
		wishlist.setReceiver(receiver);
		wishlistRepository.save(wishlist);
		return wishlist;
	}

	private List<Wish> createWishes(WishRepository wishRepository, Wishlist wishlist) {

		final String[] wishTitle = { "X Box", "Game(s)", "Controller", "Money" };
		final String[] wishDescription = { "I definitely need this! I dream of an X Box since years!",
				"From the top 10 list, I have #1 and #3. I don't like #4 and #9. Please choose what you want from the rest!",
				"Should be a very stable and reactive controller. I've already tried the one linked below andI liked it!",
				"Untold riches!!!!!!!!!" };
		final String[] wishLink = {
				"https://www.idealo.de/preisvergleich/OffersOfProduct/3922755_-xbox-one-microsoft.html",
				"https://www.spiegel.de/netzwelt/games/best-of-2019-die-zehn-besten-videospiele-des-jahres-a-1298479.html",
				"https://www.pcgamer.com/best-controller-for-pc-gaming/", "" };
		final String[] wishImage = { "", "", "XBox-Controller.png", "" };

		List<Wish> wishes = new ArrayList<>();
		for (int i = 0; i < wishTitle.length; i++) {
			Wish wish = new Wish();
			wish.setTitle(wishTitle[i]);
			wish.setDescription(wishDescription[i]);
			wish.setLink(wishLink[i]);
			wish.setImage(wishImage[i]);
			wish.setWishlist(wishlist);
			wishRepository.save(wish);
			wishes.add(wish);
		}
		return wishes;
	}

	// **************************************************************************************************
	// Empty DB tables - currently not needed
	// **************************************************************************************************
	private void emptyUserTable(UserRepository userRepository) {
		// Only possible if user is not entered in wishlist
		if (userRepository.count() != 0) {
			System.out.println("User table has at least one entry... empty table.");
			userRepository.deleteAll();
			userRepository.flush();
		}
	}

	private void emptyThemeTable(ThemeRepository themeRepository) {
		// Only possible if theme is not entered in wishlist
		if (themeRepository.count() != 0) {
			System.out.println("Theme table has at least one entry... empty table.");
			themeRepository.deleteAll();
			themeRepository.flush();
		}
	}

	private void emptyWishlistTable(WishlistRepository wishlistRepository) {
		if (wishlistRepository.count() != 0) {
			System.out.println("Wishlist table has at least one entry... empty table.");
			wishlistRepository.deleteAll();
			wishlistRepository.flush();
		}
	}

}
