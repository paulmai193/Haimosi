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
 * The Class DevicePOJO.
 * 
 * @author Paul Mai
 */
@Entity
@Table(name = "scale", catalog = "paulmai")
@DynamicUpdate(value = true)
@logia.utility.json.annotaion.JsonObject
public class ScalePOJO implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The id device. */
	@Id
	@GeneratedValue
	@Column(name = "idscale", nullable = false)
	@JsonKey(key = ParamDefine.SCALE_ID)
	private Integer           idScale;

	/** The specification. */
	@Column(name = "specification", length = 50, nullable = true)
	@JsonKey(key = ParamDefine.SCALE_SPECIFICATION)
	private String            specification;

	/** The parameter. */
	@Column(name = "parameter", length = 50, nullable = true)
	@JsonKey(key = ParamDefine.SCALE_PARAMETER)
	private String            parameter;

	/** The position. */
	@Column(name = "position", length = 50, nullable = true)
	@JsonKey(key = ParamDefine.SCALE_POSITION)
	private String            position;

	/** The metta. */
	@Column(name = "metta", length = 50, nullable = true)
	@JsonKey(key = ParamDefine.SCALE_METTA)
	private String            metta;

	/**
	 * Instantiates a new scale pojo.
	 */
	public ScalePOJO() {

	}

	/**
	 * Instantiates a new scale pojo.
	 *
	 * @param id the id
	 * @param specification the specification
	 * @param parameter the parameter
	 * @param position the position
	 * @param metta the metta
	 * @param port the port
	 * @param color the color
	 */
	public ScalePOJO(Integer id, String specification, String parameter, String position, String metta) {
		this.idScale = id;
		this.metta = metta;
		this.parameter = parameter;
		this.position = position;
		this.specification = specification;
	}

	/**
	 * Gets the id scale.
	 *
	 * @return the idScale
	 */
	public Integer getIdScale() {
		return this.idScale;
	}

	/**
	 * Gets the metta.
	 *
	 * @return the metta
	 */
	public String getMetta() {
		return this.metta;
	}

	/**
	 * Gets the parameter.
	 *
	 * @return the parameter
	 */
	public String getParameter() {
		return this.parameter;
	}

	/**
	 * Gets the position.
	 *
	 * @return the position
	 */
	public String getPosition() {
		return this.position;
	}

	/**
	 * Gets the specification.
	 *
	 * @return the specification
	 */
	public String getSpecification() {
		return this.specification;
	}

	/**
	 * Sets the id scale.
	 *
	 * @param idScale the idScale to set
	 */
	public void setIdScale(Integer idScale) {
		this.idScale = idScale;
	}

	/**
	 * Sets the metta.
	 *
	 * @param metta the metta to set
	 */
	public void setMetta(String metta) {
		this.metta = metta;
	}

	/**
	 * Sets the parameter.
	 *
	 * @param parameter the parameter to set
	 */
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	/**
	 * Sets the position.
	 *
	 * @param position the position to set
	 */
	public void setPosition(String position) {
		this.position = position;
	}

	/**
	 * Sets the specification.
	 *
	 * @param specification the specification to set
	 */
	public void setSpecification(String specification) {
		this.specification = specification;
	}

	/**
	 * To json.
	 *
	 * @return the json object
	 */
	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.addProperty(ParamDefine.SCALE_ID, this.idScale);
		json.addProperty(ParamDefine.SCALE_METTA, this.metta);
		json.addProperty(ParamDefine.SCALE_PARAMETER, this.parameter);
		json.addProperty(ParamDefine.SCALE_POSITION, this.position);
		json.addProperty(ParamDefine.SCALE_SPECIFICATION, this.specification);

		return json;
	}

}
