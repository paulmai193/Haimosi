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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import logia.utility.json.annotaion.JsonKey;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.DynamicUpdate;

import com.google.gson.JsonObject;
import com.haimosi.param.ParamDefine;

/**
 * The Class UserPOJO.
 * 
 * @author Paul Mai
 */
@Entity
@Table(name = "user")
@DynamicUpdate(value = true)
@logia.utility.json.annotaion.JsonObject
public class UserPOJO implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long    serialVersionUID = 1L;

	/** The avatar. */
	@Column(name = "avatar", nullable = true, length = 50)
	@JsonKey(key = ParamDefine.AVATAR)
	private String               avatar;

	/** The credit account. */
	@OneToOne(fetch = FetchType.EAGER, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private CreditAccountPOJO    creditAccount;

	/** The email. */
	@Column(name = "email", nullable = false, length = 40)
	@JsonKey(key = ParamDefine.EMAIL)
	private String               email;

	/** The first name. */
	@Column(name = "firstname", nullable = false, length = 16)
	@JsonKey(key = ParamDefine.FIRST_NAME)
	private String               firstName;

	/** The id user. */
	@Id
	@GeneratedValue
	@Column(name = "iduser", nullable = false)
	@JsonKey(key = ParamDefine.USER_ID)
	private Integer              idUser;

	/** The last name. */
	@Column(name = "lastname", nullable = false, length = 16)
	@JsonKey(key = ParamDefine.LAST_NAME)
	private String               lastName;

	/** The likes. */
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "user_like", catalog = "paulmai", joinColumns = { @JoinColumn(name = "iduser", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "idtransaction", nullable = false, updatable = false) })
	private Set<TransactionPOJO> likes            = new HashSet<TransactionPOJO>();

	/** The password. */
	@Column(name = "password", nullable = false, length = 40)
	private String               password;

	/** The phone. */
	@Column(name = "phone", nullable = false, length = 16)
	@JsonKey(key = ParamDefine.PHONE)
	private String               phone;

	/** The role. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idrole", nullable = false)
	private RolePOJO             role;

	/** The status. */
	@Column(name = "status", nullable = false)
	private byte                 status;

	/** The mapping transactions. */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<TransactionPOJO> transactions     = new HashSet<TransactionPOJO>();

	/** The verify code. */
	@Column(name = "verifycode", nullable = true, length = 6)
	private String               verifyCode;

	/**
	 * Instantiates a new user pojo.
	 */
	public UserPOJO() {

	}

	/**
	 * Instantiates a new user pojo.
	 *
	 * @param idUser the id user
	 * @param firstName the first name
	 * @param lastName the last name
	 * @param phone the phone
	 * @param email the email
	 * @param password the password
	 * @param verifycode the verifycode
	 * @param status the status
	 * @param role the role
	 * @param creditAccount the credit account
	 * @param avatar the avatar
	 */
	public UserPOJO(Integer idUser, String firstName, String lastName, String phone, String email, String password, String verifycode, byte status,
	        RolePOJO role, CreditAccountPOJO creditAccount, String avatar) {
		this.idUser = idUser;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phone = phone;
		this.email = email;
		this.password = password;
		this.verifyCode = verifycode;
		this.status = status;
		this.role = role;
		this.creditAccount = creditAccount;
		this.avatar = avatar;
	}

	/**
	 * Adds the like to transaction.
	 *
	 * @param transaction the transaction
	 * @return true, if successful
	 */
	public boolean addLike(TransactionPOJO transaction) {
		return this.likes.add(transaction);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof UserPOJO) {
			UserPOJO compareUser = (UserPOJO) obj;
			EqualsBuilder builder = new EqualsBuilder();
			builder.append(this.getStatus(), compareUser.getStatus()).append(this.getEmail(), compareUser.getEmail())
			        .append(this.getFirstName(), compareUser.getFirstName()).append(this.getIdUser(), compareUser.getIdUser())
			        .append(this.getLastName(), compareUser.getLastName()).append(this.getPassword(), compareUser.getPassword());
			return builder.isEquals();
		}
		else {
			return false;
		}
	}

	/**
	 * Gets the avatar.
	 *
	 * @return the avatar
	 */
	public String getAvatar() {
		return this.avatar;
	}

	/**
	 * Gets the credit account.
	 *
	 * @return the creditAccount
	 */
	public CreditAccountPOJO getCreditAccount() {
		return this.creditAccount;
	}

	/**
	 * Gets the email.
	 *
	 * @return the email
	 */
	public String getEmail() {
		return this.email;
	}

	/**
	 * Gets the first name.
	 *
	 * @return the firstName
	 */
	public String getFirstName() {
		return this.firstName;
	}

	/**
	 * Gets the id user.
	 *
	 * @return the idUser
	 */
	public Integer getIdUser() {
		return this.idUser;
	}

	/**
	 * Gets the last name.
	 *
	 * @return the lastName
	 */
	public String getLastName() {
		return this.lastName;
	}

	/**
	 * Gets the likes.
	 *
	 * @return the likes
	 */
	public Set<TransactionPOJO> getLikes() {
		return this.likes;
	}

	/**
	 * Gets the password.
	 *
	 * @return the password
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * Gets the phone.
	 *
	 * @return the phone
	 */
	public String getPhone() {
		return this.phone;
	}

	/**
	 * Gets the role.
	 *
	 * @return the role
	 */
	public RolePOJO getRole() {
		return this.role;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public byte getStatus() {
		return this.status;
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
	 * Gets the verify code.
	 *
	 * @return the verifyCode
	 */
	public String getVerifyCode() {
		return this.verifyCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder(17, 31);
		builder.append(this.getStatus()).append(this.getEmail()).append(this.getFirstName()).append(this.getIdUser()).append(this.getLastName())
		        .append(this.getPassword()).append(this.getPassword()).append(this.getPhone());
		return builder.toHashCode();
	}

	/**
	 * Removes the like.
	 *
	 * @param item the item
	 * @return true, if successful
	 */
	public boolean removeLike(ItemPOJO item) {
		return this.likes.remove(item);
	}

	/**
	 * Sets the avatar.
	 *
	 * @param avatar the avatar to set
	 */
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	/**
	 * Sets the credit account.
	 *
	 * @param creditAccount the creditAccount to set
	 */
	public void setCreditAccount(CreditAccountPOJO creditAccount) {
		this.creditAccount = creditAccount;
	}

	/**
	 * Sets the email.
	 *
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Sets the first name.
	 *
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Sets the id user.
	 *
	 * @param idUser the idUser to set
	 */
	public void setIdUser(Integer idUser) {
		this.idUser = idUser;
	}

	/**
	 * Sets the last name.
	 *
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Sets the likes.
	 *
	 * @param likes the likes to set
	 */
	public void setLikes(Set<TransactionPOJO> likes) {
		this.likes = likes;
	}

	/**
	 * Sets the password.
	 *
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Sets the phone.
	 *
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * Sets the role.
	 *
	 * @param role the role to set
	 */
	public void setRole(RolePOJO role) {
		this.role = role;
	}

	/**
	 * Sets the status.
	 *
	 * @param status the status to set
	 */
	public void setStatus(byte status) {
		this.status = status;
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
	 * Sets the verify code.
	 *
	 * @param verifyCode the verifyCode to set
	 */
	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}

	/**
	 * To json.
	 *
	 * @return the json object
	 */
	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.addProperty(ParamDefine.USER_ID, this.idUser);
		json.addProperty(ParamDefine.FIRST_NAME, this.firstName);
		json.addProperty(ParamDefine.LAST_NAME, this.lastName);
		json.addProperty(ParamDefine.PHONE, this.phone);
		json.addProperty(ParamDefine.EMAIL, this.email);
		if (this.avatar != null) {
			json.addProperty(ParamDefine.AVATAR, this.avatar);
		}
		else {
			json.addProperty(ParamDefine.AVATAR, "");
		}
		return json;
	}
}
