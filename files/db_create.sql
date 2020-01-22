-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema gift_me_five
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema gift_me_five
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `gift_me_five` DEFAULT CHARACTER SET utf8 ;
USE `gift_me_five` ;

-- -----------------------------------------------------
-- Table `gift_me_five`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gift_me_five`.`user` (
  `id` BIGINT NOT NULL,
  `login` VARCHAR(45) NOT NULL,
  `password` VARCHAR(45) NOT NULL,
  `firstname` VARCHAR(45) NULL,
  `lastname` VARCHAR(45) NULL,
  `email` VARCHAR(45) NULL,
  `create_date` DATETIME NULL,
  `modify_date` DATETIME NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `login_UNIQUE` (`login` ASC),
  UNIQUE INDEX `iduser_UNIQUE` (`id` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `gift_me_five`.`theme`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gift_me_five`.`theme` (
  `id` BIGINT NOT NULL,
  `backgroundPicture` VARCHAR(45) NOT NULL,
  `category` VARCHAR(45) NULL COMMENT 'defaults (spring, summer, autumn, winter)\n		',
  `modify_date` DATETIME NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `idTheme_UNIQUE` (`id` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `gift_me_five`.`wishlist`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gift_me_five`.`wishlist` (
  `id` BIGINT NOT NULL,
  `theme_id` BIGINT NOT NULL,
  `unique_url_giver` VARCHAR(45) NULL,
  `unique_url_receiver` VARCHAR(45) NULL,
  `create_date` DATETIME NULL,
  `modify_date` DATETIME NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_wishlist_theme1_idx` (`theme_id` ASC),
  CONSTRAINT `fk_wishlist_theme1`
    FOREIGN KEY (`theme_id`)
    REFERENCES `gift_me_five`.`theme` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `gift_me_five`.`wish`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gift_me_five`.`wish` (
  `id` BIGINT NOT NULL,
  `title` VARCHAR(45) NULL,
  `item` VARCHAR(45) NULL,
  `description` VARCHAR(45) NULL,
  `link` VARCHAR(45) NULL,
  `image` VARCHAR(45) NULL,
  `create_date` DATETIME NULL,
  `modify_date` DATETIME NULL,
  `giver_id_foreign` BIGINT NULL,
  `wishlist_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `idReceiver_UNIQUE` (`id` ASC),
  INDEX `fk_wish_user1_idx` (`giver_id_foreign` ASC),
  INDEX `fk_wish_wishlist1_idx` (`wishlist_id` ASC),
  CONSTRAINT `fk_wish_user1`
    FOREIGN KEY (`giver_id_foreign`)
    REFERENCES `gift_me_five`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_wish_wishlist1`
    FOREIGN KEY (`wishlist_id`)
    REFERENCES `gift_me_five`.`wishlist` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `gift_me_five`.`user_has_wishlist`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gift_me_five`.`user_has_wishlist` (
  `user_id` BIGINT NOT NULL,
  `wishlist_id` BIGINT NOT NULL,
  `giver_receiver` TINYINT(1) NOT NULL,
  PRIMARY KEY (`user_id`, `wishlist_id`),
  INDEX `fk_user_has_wishlist1_wishlist1_idx` (`wishlist_id` ASC),
  INDEX `fk_user_has_wishlist1_user1_idx` (`user_id` ASC),
  CONSTRAINT `fk_user_has_wishlist1_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `gift_me_five`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_has_wishlist1_wishlist1`
    FOREIGN KEY (`wishlist_id`)
    REFERENCES `gift_me_five`.`wishlist` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
