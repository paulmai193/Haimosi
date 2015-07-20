package com.haimosi.hibernate.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import logia.utility.json.annotaion.JsonKey;

import org.hibernate.annotations.DynamicUpdate;

import com.google.gson.JsonObject;
import com.haimosi.param.ParamDefine;

/**
 * The Class LightPOJO.
 * 
 * @author Paul Mai
 */
@Entity
@Table(name = "light", catalog = "paulmai")
@DynamicUpdate(value = true)
@logia.utility.json.annotaion.JsonObject
public class LightPOJO implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The color. */
	@Column(name = "color", length = 50, nullable = true)
	@JsonKey(key = ParamDefine.LIGHT_COLOR)
	private String            color;

	/** The id light. */
	@Id
	@GeneratedValue
	@Column(name = "idlight", nullable = false)
	@JsonKey(key = ParamDefine.LIGHT_ID)
	private Integer           idLight;

	/** The port. */
	@Column(name = "port", length = 5, nullable = true)
	@JsonKey(key = ParamDefine.LIGHT_PORT)
	private int               port;

	/**
	 * Instantiates a new light pojo.
	 */
	public LightPOJO() {

	}

	/**
	 * Instantiates a new light pojo.
	 *
	 * @param id the id
	 * @param color the color
	 * @param port the port
	 */
	public LightPOJO(Integer id, String color, int port) {
		this.idLight = id;
		this.color = color;
		this.port = port;
	}

	/**
	 * Gets the color.
	 *
	 * @return the color
	 */
	public String getColor() {
		return this.color;
	}

	/**
	 * Gets the port.
	 *
	 * @return the port
	 */
	public int getPort() {
		return this.port;
	}

	/**
	 * Sets the color.
	 *
	 * @param color the color to set
	 */
	public void setColor(String color) {
		this.color = color;
	}

	/**
	 * Sets the port.
	 *
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * To json.
	 *
	 * @return the json object
	 */
	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.addProperty(ParamDefine.LIGHT_COLOR, this.color);
		json.addProperty(ParamDefine.LIGHT_ID, this.idLight);
		json.addProperty(ParamDefine.LIGHT_PORT, this.port);

		return json;
	}

}
