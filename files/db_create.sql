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
  `idUser` INT NOT NULL AUTO_INCREMENT,
  `login` VARCHAR(45) NOT NULL,
  `password` VARCHAR(45) NOT NULL,
  `firstname` VARCHAR(45) NULL,
  `lastname` VARCHAR(45) NULL,
  `email` VARCHAR(45) NULL,
  `create_date` DATE NULL,
  `modify_date` DATE NULL,
  PRIMARY KEY (`idUser`),
  UNIQUE INDEX `login_UNIQUE` (`login` ASC),
  UNIQUE INDEX `iduser_UNIQUE` (`idUser` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `gift_me_five`.`theme`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gift_me_five`.`theme` (
  `idTheme` INT NOT NULL AUTO_INCREMENT,
  `backgroupdPicture` VARCHAR(45) NOT NULL,
  `category` VARCHAR(45) NULL COMMENT 'defaults (spring, summer, autumn, winter)\n',
  PRIMARY KEY (`idTheme`),
  UNIQUE INDEX `idTheme_UNIQUE` (`idTheme` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `gift_me_five`.`wishlist`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gift_me_five`.`wishlist` (
  `idwishlist` INT NOT NULL AUTO_INCREMENT,
  `theme_idTheme` INT NOT NULL,
  `create_date` DATE NULL,
  `update_date` DATE NULL,
  `unique_url_giver` VARCHAR(45) NULL,
  `unique_url_receiver` VARCHAR(45) NULL,
  `modify_date` DATE NULL,
  PRIMARY KEY (`idwishlist`),
  INDEX `fk_wishlist_theme1_idx` (`theme_idTheme` ASC),
  CONSTRAINT `fk_wishlist_theme1`
    FOREIGN KEY (`theme_idTheme`)
    REFERENCES `gift_me_five`.`theme` (`idTheme`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `gift_me_five`.`wish`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gift_me_five`.`wish` (
  `idWish` INT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(45) NULL,
  `item` VARCHAR(45) NULL,
  `description` VARCHAR(45) NULL,
  `link` VARCHAR(45) NULL,
  `image` VARCHAR(45) NULL,
  `create_date` DATE NULL,
  `modify_date` DATE NULL,
  `giver_id_foreign` INT NOT NULL,
  `wishlist_idwishlist` INT NOT NULL,
  PRIMARY KEY (`idWish`),
  UNIQUE INDEX `idReceiver_UNIQUE` (`idWish` ASC),
  INDEX `fk_wish_user1_idx` (`giver_id_foreign` ASC),
  INDEX `fk_wish_wishlist1_idx` (`wishlist_idwishlist` ASC),
  CONSTRAINT `fk_wish_user1`
    FOREIGN KEY (`giver_id_foreign`)
    REFERENCES `gift_me_five`.`user` (`idUser`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_wish_wishlist1`
    FOREIGN KEY (`wishlist_idwishlist`)
    REFERENCES `gift_me_five`.`wishlist` (`idwishlist`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `gift_me_five`.`timestamps`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gift_me_five`.`timestamps` (
  `create_time`  NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time`  NULL);


-- -----------------------------------------------------
-- Table `gift_me_five`.`user_has_wishlist`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gift_me_five`.`user_has_wishlist` (
  `user_idUser` INT NOT NULL,
  `wishlist_idwishlist` INT NOT NULL,
  `giver_receiver` TINYINT(1) NULL,
  PRIMARY KEY (`user_idUser`, `wishlist_idwishlist`),
  INDEX `fk_user_has_wishlist_wishlist1_idx` (`wishlist_idwishlist` ASC),
  INDEX `fk_user_has_wishlist_user1_idx` (`user_idUser` ASC),
  CONSTRAINT `fk_user_has_wishlist_user1`
    FOREIGN KEY (`user_idUser`)
    REFERENCES `gift_me_five`.`user` (`idUser`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_has_wishlist_wishlist1`
    FOREIGN KEY (`wishlist_idwishlist`)
    REFERENCES `gift_me_five`.`wishlist` (`idwishlist`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
