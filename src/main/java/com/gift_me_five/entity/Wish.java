package com.gift_me_five.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Wish {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String title;
	
	@Column(columnDefinition = "TEXT")
	private String item;
	
	@Column(columnDefinition = "TEXT")
	private String description;
	
	@Column(columnDefinition = "TEXT")
	private String link;
	
	private Float price;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "createDate", updatable = false, nullable = true, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Date createDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modifyDate", updatable = false, nullable = true, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	private Date modifyDate;

	@ManyToOne
    @JoinColumn(name = "giverId")
    private User giver;

	@ManyToOne
    @JoinColumn(name = "wishlistId", nullable=false)
    private  Wishlist wishlist;
	
	@Lob
	private byte[] picture;

	
	public Wish() {
		this.price = 0F;
	}	
}
