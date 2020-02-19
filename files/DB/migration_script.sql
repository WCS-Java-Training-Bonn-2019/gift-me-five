-- ----------------------------------------------------------------------------
-- MySQL Workbench Migration
-- Migrated Schemata: gift_me_five
-- Source Schemata: gift_me_five
-- Created: Wed Feb 19 17:03:43 2020
-- Workbench Version: 6.3.8
-- ----------------------------------------------------------------------------

SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------------------------------------------------------
-- Schema gift_me_five
-- ----------------------------------------------------------------------------
DROP SCHEMA IF EXISTS `gift_me_five` ;
CREATE SCHEMA IF NOT EXISTS `gift_me_five` ;

-- ----------------------------------------------------------------------------
-- Table gift_me_five.role
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `gift_me_five`.`role` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `create_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modify_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `role` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 5
DEFAULT CHARACTER SET = latin1;

-- ----------------------------------------------------------------------------
-- Table gift_me_five.theme
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `gift_me_five`.`theme` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `background_picture` VARCHAR(255) NULL DEFAULT NULL,
  `modify_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 15
DEFAULT CHARACTER SET = utf8;

-- ----------------------------------------------------------------------------
-- Table gift_me_five.user
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `gift_me_five`.`user` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `create_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `email` VARCHAR(255) NOT NULL,
  `failed_logins` BIGINT(20) NULL DEFAULT NULL,
  `firstname` VARCHAR(255) NULL DEFAULT NULL,
  `last_login` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastname` VARCHAR(255) NULL DEFAULT NULL,
  `modify_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `password` VARCHAR(255) NOT NULL,
  `role` VARCHAR(255) NULL DEFAULT NULL,
  `reason` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `UK_ob8kqyqqgmefl0aco34akdtpe` (`email` ASC))
ENGINE = InnoDB
AUTO_INCREMENT = 29
DEFAULT CHARACTER SET = utf8;

-- ----------------------------------------------------------------------------
-- Table gift_me_five.wish
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `gift_me_five`.`wish` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `create_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `description` TEXT NULL DEFAULT NULL,
  `link` TEXT NULL DEFAULT NULL,
  `modify_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `price` FLOAT NULL DEFAULT NULL,
  `title` VARCHAR(255) NULL DEFAULT NULL,
  `giver_id` BIGINT(20) NULL DEFAULT NULL,
  `wishlist_id` BIGINT(20) NOT NULL,
  `picture` LONGBLOB NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FKaoudos8e3i8b1b5mt2fr7ami` (`giver_id` ASC),
  INDEX `FKf2656374n9at6vxifmpphd9fr` (`wishlist_id` ASC),
  CONSTRAINT `FKaoudos8e3i8b1b5mt2fr7ami`
    FOREIGN KEY (`giver_id`)
    REFERENCES `gift_me_five`.`user` (`id`),
  CONSTRAINT `FKf2656374n9at6vxifmpphd9fr`
    FOREIGN KEY (`wishlist_id`)
    REFERENCES `gift_me_five`.`wishlist` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 119
DEFAULT CHARACTER SET = utf8;

-- ----------------------------------------------------------------------------
-- Table gift_me_five.wishlist
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `gift_me_five`.`wishlist` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `create_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modify_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `title` VARCHAR(255) NULL DEFAULT NULL,
  `unique_url_giver` VARCHAR(255) NULL DEFAULT NULL,
  `unique_url_receiver` VARCHAR(255) NULL DEFAULT NULL,
  `receiver_id` BIGINT(20) NOT NULL,
  `theme_id` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FKpuosnphoi0lacly74rpm89p5a` (`receiver_id` ASC),
  INDEX `FKfx21yg2sjppvs4ei80qeey96l` (`theme_id` ASC),
  CONSTRAINT `FKfx21yg2sjppvs4ei80qeey96l`
    FOREIGN KEY (`theme_id`)
    REFERENCES `gift_me_five`.`theme` (`id`),
  CONSTRAINT `FKpuosnphoi0lacly74rpm89p5a`
    FOREIGN KEY (`receiver_id`)
    REFERENCES `gift_me_five`.`user` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 77
DEFAULT CHARACTER SET = utf8;

-- ----------------------------------------------------------------------------
-- Table gift_me_five.wishlist_givers
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `gift_me_five`.`wishlist_givers` (
  `wishlist_id` BIGINT(20) NOT NULL,
  `user_id` BIGINT(20) NOT NULL,
  INDEX `FKfildxaat5jyo9ugd5ukuvdxt4` (`user_id` ASC),
  INDEX `FK4lo563o1asoldh3cla4e9vddv` (`wishlist_id` ASC),
  CONSTRAINT `FK4lo563o1asoldh3cla4e9vddv`
    FOREIGN KEY (`wishlist_id`)
    REFERENCES `gift_me_five`.`wishlist` (`id`),
  CONSTRAINT `FKfildxaat5jyo9ugd5ukuvdxt4`
    FOREIGN KEY (`user_id`)
    REFERENCES `gift_me_five`.`user` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;
SET FOREIGN_KEY_CHECKS = 1;
