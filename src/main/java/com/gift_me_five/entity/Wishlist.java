package com.gift_me_five.entity;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Wishlist {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String unique_url_giver;
	private String unique_url_receiver;
	private Date create_date;
	private Date modify_date;

	public Wishlist() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUnique_url_giver() {
		return unique_url_giver;
	}

	public void setUnique_url_giver(String unique_url_giver) {
		this.unique_url_giver = unique_url_giver;
	}

	public String getUnique_url_receiver() {
		return unique_url_receiver;
	}

	public void setUnique_url_receiver(String unique_url_receiver) {
		this.unique_url_receiver = unique_url_receiver;
	}

	public Date getCreate_date() {
		return create_date;
	}

	public void setCreate_date(Date create_date) {
		this.create_date = create_date;
	}

	public Date getModify_date() {
		return modify_date;
	}

	public void setModify_date(Date modify_date) {
		this.modify_date = modify_date;
	}
	
}
