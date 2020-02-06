package com.gift_me_five.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gift_me_five.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

}

