package com.gift_me_five.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gift_me_five.entity.User;

@Repository
public interface RoleRepository extends JpaRepository<User, Long> {

}

