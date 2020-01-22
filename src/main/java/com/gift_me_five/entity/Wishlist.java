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
	private String uniqueUrlGiver;
	private String uniqueUrlReceiver;
	private Date createDate;
	private Date modifyDate;

	public Wishlist() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUnique_url_giver() {
		return uniqueUrlGiver;
	}

	public void setUnique_url_giver(String unique_url_giver) {
		this.uniqueUrlGiver = unique_url_giver;
	}

	public String getUnique_url_receiver() {
		return uniqueUrlReceiver;
	}

	public void setUnique_url_receiver(String unique_url_receiver) {
		this.uniqueUrlReceiver = unique_url_receiver;
	}

	public Date getCreate_date() {
		return createDate;
	}

	public void setCreate_date(Date create_date) {
		this.createDate = create_date;
	}

	public Date getModify_date() {
		return modifyDate;
	}

	public void setModify_date(Date modify_date) {
		this.modifyDate = modify_date;
	}
	
}
