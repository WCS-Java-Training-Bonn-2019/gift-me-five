package com.gift_me_five.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gift_me_five.entity.Wish;

@Repository
public interface WishRepository extends JpaRepository<Wish, Long> {
}
