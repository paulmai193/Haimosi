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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

/**
 * The Class RolePOJO.
 * 
 * @author Paul Mai
 */
@Entity
@Table(name = "role", catalog = "paulmai")
@DynamicUpdate(value = true)
public class RolePOJO implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The description. */
	@Column(name = "description", nullable = true, length = 50)
	private String            description;

	/** The id role. */
	@Id
	@GeneratedValue
	@Column(name = "idrole", nullable = false)
	private Integer           idRole;

	/** The name. */
	@Column(name = "name", nullable = false, length = 16)
	private String            name;

	/** The mapping users. */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<UserPOJO>     users            = new HashSet<UserPOJO>();

	/**
	 * Instantiates a new role pojo.
	 */
	public RolePOJO() {

	}

	/**
	 * Instantiates a new role pojo.
	 *
	 * @param idRole the id role
	 * @param name the name
	 * @param description the description
	 */
	public RolePOJO(Integer idRole, String name, String description) {
		this.idRole = idRole;
		this.name = name;
		this.description = description;
	}

	public RolePOJO(Integer idRole, String name, String description, Set<UserPOJO> users) {
		this.idRole = idRole;
		this.name = name;
		this.description = description;
		this.users = users;
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
	 * Gets the id role.
	 *
	 * @return the idRole
	 */
	public Integer getIdRole() {
		return this.idRole;
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
	 * Gets the users by this role.
	 *
	 * @return the users
	 */
	public Set<UserPOJO> getUsers() {
		return this.users;
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
	 * Sets the id role.
	 *
	 * @param idRole the idRole to set
	 */
	public void setIdRole(Integer idRole) {
		this.idRole = idRole;
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
	 * Sets the users.
	 *
	 * @param users the users to set
	 */
	public void setUsers(Set<UserPOJO> users) {
		this.users.addAll(users);
	}
}
