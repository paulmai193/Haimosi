package com.haimosi.hibernate.pojo;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import logia.utility.json.annotaion.JsonKey;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.DynamicUpdate;

import com.google.gson.JsonObject;
import com.haimosi.param.ParamDefine;

/**
 * The Class ItemPOJO.
 * 
 * @author Paul Mai
 */
@Entity
@Table(name = "item", catalog = "paulmai")
@DynamicUpdate(value = true)
@logia.utility.json.annotaion.JsonObject
public class ItemPOJO implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long    serialVersionUID = 1L;

	/** The id item. */
	@Id
	@GeneratedValue
	@Column(name = "iditem", nullable = false)
	@JsonKey(key = ParamDefine.ITEM_ID)
	private Integer              idItem;

	/** The name. */
	@Column(name = "name", nullable = false, length = 30)
	@JsonKey(key = ParamDefine.ITEM_NAME)
	private String               name;

	/** The description. */
	@Column(name = "description", columnDefinition = "TEXT", nullable = true)
	@JsonKey(key = ParamDefine.ITEM_DESCRIPTION)
	private String               description;

	/** The price. */
	@Column(name = "price", nullable = false)
	@JsonKey(key = ParamDefine.ITEM_PRICE)
	private float                price;

	/** The unit. */
	@Column(name = "unit", nullable = false, length = 5)
	@JsonKey(key = ParamDefine.ITEM_UNIT)
	private String               unit;

	/** The photo. */
	@Column(name = "photo", nullable = true, length = 50)
	@JsonKey(key = ParamDefine.ITEM_PHOTO)
	private String               photo;

	/** The basic amount. */
	@Column(name = "basicamount", nullable = false)
	@JsonKey(key = ParamDefine.ITEM_BASIC_AMOUNT)
	private float                basicAmount;

	/** The transactions. */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<TransactionPOJO> transactions     = new HashSet<TransactionPOJO>();

	/** The likes. */
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "user_like", catalog = "paulmai", joinColumns = { @JoinColumn(name = "iditem", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "iduser", nullable = false, updatable = false) })
	private Set<UserPOJO>        likes            = new HashSet<UserPOJO>();

	/**
	 * Instantiates a new item pojo.
	 */
	public ItemPOJO() {

	}

	/**
	 * Instantiates a new item pojo.
	 *
	 * @param idItem the id item
	 * @param name the name
	 * @param description the description
	 * @param price the price
	 * @param unit the unit
	 * @param photo the photo
	 * @param basicAmount the basic amount
	 */
	public ItemPOJO(Integer idItem, String name, String description, float price, String unit, String photo, float basicAmount) {
		this.idItem = idItem;
		this.name = name;
		this.description = description;
		this.price = price;
		this.unit = unit;
		this.photo = photo;
		this.basicAmount = basicAmount;
	}

	/**
	 * Gets the basic amount.
	 *
	 * @return the basicAmount
	 */
	public float getBasicAmount() {
		return this.basicAmount;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Gets the id item.
	 *
	 * @return the idItem
	 */
	public Integer getIdItem() {
		return this.idItem;
	}

	/**
	 * Gets the likes.
	 *
	 * @return the likes
	 */
	public Set<UserPOJO> getLikes() {
		return this.likes;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Gets the num like.
	 *
	 * @return the num like
	 */
	@JsonKey(key = ParamDefine.ITEM_NUM_LIKE)
	public int getNumLike() {
		return this.getLikes().size();
	}

	/**
	 * Gets the photo.
	 *
	 * @return the photo
	 */
	public String getPhoto() {
		return this.photo;
	}

	/**
	 * Gets the price.
	 *
	 * @return the price
	 */
	public float getPrice() {
		return this.price;
	}

	/**
	 * Gets the transactions.
	 *
	 * @return the transactions
	 */
	public Set<TransactionPOJO> getTransactions() {
		return this.transactions;
	}

	/**
	 * Gets the unit.
	 *
	 * @return the unit
	 */
	public String getUnit() {
		return this.unit;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder(17, 31);
		builder.append(this.price).append(this.description).append(this.idItem).append(this.name).append(this.photo).append(this.unit);
		return builder.toHashCode();
	}

	/**
	 * Checks if is like.
	 *
	 * @param user the user
	 * @return true, if is like
	 */
	public boolean isLike(UserPOJO user) {
		return this.getLikes().contains(user);
	}

	/**
	 * Sets the basic amount.
	 *
	 * @param basicAmount the basicAmount to set
	 */
	public void setBasicAmount(float basicAmount) {
		this.basicAmount = basicAmount;
	}

	/**
	 * Sets the description.
	 *
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Sets the id item.
	 *
	 * @param idItem the idItem to set
	 */
	public void setIdItem(Integer idItem) {
		this.idItem = idItem;
	}

	/**
	 * Sets the likes.
	 *
	 * @param likes the likes to set
	 */
	public void setLikes(Set<UserPOJO> likes) {
		this.likes = likes;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the photo.
	 *
	 * @param photo the photo to set
	 */
	public void setPhoto(String photo) {
		this.photo = photo;
	}

	/**
	 * Sets the price.
	 *
	 * @param price the price to set
	 */
	public void setPrice(float price) {
		this.price = price;
	}

	/**
	 * Sets the transactions.
	 *
	 * @param transactions the transactions to set
	 */
	public void setTransactions(Set<TransactionPOJO> transactions) {
		this.transactions.addAll(transactions);
	}

	/**
	 * Sets the unit.
	 *
	 * @param unit the unit to set
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}

	/**
	 * To json.
	 *
	 * @return the json object
	 */
	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.addProperty(ParamDefine.ITEM_DESCRIPTION, this.description);
		json.addProperty(ParamDefine.ITEM_ID, this.idItem);
		json.addProperty(ParamDefine.ITEM_NAME, this.name);
		if (this.photo != null) {
			json.addProperty(ParamDefine.ITEM_PHOTO, this.photo);
		}
		else {
			json.addProperty(ParamDefine.ITEM_PHOTO, "");
		}
		json.addProperty(ParamDefine.ITEM_PRICE, this.price);
		json.addProperty(ParamDefine.ITEM_UNIT, this.unit);

		return json;
	}
}
