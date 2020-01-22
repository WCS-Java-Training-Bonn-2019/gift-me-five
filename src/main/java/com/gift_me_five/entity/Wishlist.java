package com.gift_me_five.entity;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@Entity
public class Wishlist {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String uniqueUrlGiver;
	private String uniqueUrlReceiver;
	private Date createDate;
	private Date modifyDate;

    @ManyToOne
    @JoinColumn(name = "receiverId", nullable=false)
    private  User user;
        
    @ManyToOne
    @JoinColumn(name = "themeId")
    private  Theme theme;
    
    @ManyToMany
    @JoinTable(name="GiverSeeWishlist", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns=@JoinColumn(name="wishlist_id"))
    private List<User> users = new ArrayList<>();
    
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

	public String getUniqueUrlGiver() {
		return uniqueUrlGiver;
	}

	public void setUniqueUrlGiver(String uniqueUrlGiver) {
		this.uniqueUrlGiver = uniqueUrlGiver;
	}

	public String getUniqueUrlReceiver() {
		return uniqueUrlReceiver;
	}

	public void setUniqueUrlReceiver(String uniqueUrlReceiver) {
		this.uniqueUrlReceiver = uniqueUrlReceiver;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Theme getTheme() {
		return theme;
	}

	public void setTheme(Theme theme) {
		this.theme = theme;
	}

	public List<Wish> getWishs() {
		return wishs;
	}

	public void setWishs(List<Wish> wishs) {
		this.wishs = wishs;
	}
	
}
