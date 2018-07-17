package hello.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "card")
public class Card implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6070990636439137410L;
	
	@Id
	private Integer id;
	@Column(name = "image")
	private String image;
	@Column(name = "owner")
	private String owner;
	@Column(name = "choosers")
	private String choosers;
	@Column(name = "used")
	private String used;
	private String showTipOrd;
	
	public String getShowTipOrd() {
		return showTipOrd;
	}

	public void setShowTipOrd(String showTipOrd) {
		this.showTipOrd = showTipOrd;
	}

	public String getUsed() {
		return used;
	}

	public void setUsed(String used) {
		this.used = used;
	}

	public Card() {
	}

	public Card(String image) {
		super();
		this.image = image;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getChoosers() {
		return choosers;
	}

	public void setChoosers(String choosers) {
		this.choosers = choosers;
	}

}