package com.gift_me_five.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Theme {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String backgroundPicture;
	private String category;

//	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	@UpdateTimestamp
	@Column(name = "modifyDate", updatable = false, nullable = true, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	private Date modifyDate;

	@OneToMany(mappedBy = "theme")
	private List<Wishlist> wishlists = new ArrayList<>();
	
	//if theme is deleted, set all wishlist theme using this theme to null
	@PreRemove
	private void removeParentFromChildren() {
		for (Wishlist child : this.wishlists) {
			child.setTheme(null);
		}
	}

	public Theme() {
	}
}
