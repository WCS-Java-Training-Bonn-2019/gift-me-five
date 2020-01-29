package com.gift_me_five.entity;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Wishlist {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String uniqueUrlGiver;
	private String uniqueUrlReceiver;
	
	private String title;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "createDate", updatable = false, nullable = true, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Date createDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modifyDate", updatable = false, nullable = true, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	private Date modifyDate;


    @ManyToOne
    @JoinColumn(name = "receiverId", nullable=false)
    private  User receiver;
        
    @ManyToOne
    @JoinColumn(name = "themeId")
    private  Theme theme;
    
    @ManyToMany
    @JoinTable(name="GiverSeeWishlist", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns=@JoinColumn(name="wishlist_id"))
    private List<User> givers = new ArrayList<>();
    
    
	public Wishlist() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public User getReceiver() {
		return receiver;
	}

	public void setReceiver(User receiver) {
		this.receiver = receiver;
	}

	public Theme getTheme() {
		return theme;
	}

	public void setTheme(Theme theme) {
		this.theme = theme;
	}

	public List<User> getGivers() {
		return givers;
	}

	public void setGivers(List<User> givers) {
		this.givers = givers;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
