package com.haimosi.api.apps;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import logia.hibernate.dao.AbstractDAO;
import logia.utility.collection.CollectionUtil;
import logia.utility.json.JsonUtil;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.haimosi.define.StatusCode;
import com.haimosi.exception.ProcessException;
import com.haimosi.hibernate.dao.ItemDAO;
import com.haimosi.hibernate.pojo.ItemPOJO;
import com.haimosi.param.FloatParam;
import com.haimosi.param.IndexParam;
import com.haimosi.param.IntegerParam;
import com.haimosi.param.ParamDefine;
import com.haimosi.pool.DAOPool;

/**
 * The Class ItemController_v1_0.
 * 
 * @author Paul Mai
 */
@Path("/api/v1.0/apps/item")
public class ItemController_v1_0 {

	/** The logger. */
	private final Logger LOGGER = Logger.getLogger(this.getClass());

	/** The http request. */
	@Context
	HttpServletRequest   httpRequest;

	/**
	 * Adds the favorite item.
	 *
	 * @param idItem the id item
	 * @return the string
	 */
	@POST
	@Path("/addfavorite")
	@Consumes(value = { MediaType.APPLICATION_FORM_URLENCODED })
	@Produces(value = { MediaType.APPLICATION_JSON })
	public String addFavorite(@FormParam(ParamDefine.ITEM_ID) IntegerParam idItem) {
		// Session session = (Session) this.httpRequest.getAttribute(ParamDefine.HIBERNATE_SESSION);
		// try (ItemDAO itemDAO = AbstractDAO.borrowFromPool(DAOPool.itemPool); UserDAO userDAO = AbstractDAO.borrowFromPool(DAOPool.userPool)) {
		// JsonObject jsonResponse = new JsonObject();
		//
		// ItemPOJO item = itemDAO.get(session, idItem.getValue());
		// if (item != null) {
		// UserPOJO user = (UserPOJO) this.httpRequest.getAttribute(ParamDefine.USER);
		// user.addLike(item);
		// userDAO.update(session, user);
		// HibernateUtil.commitTransaction(session);
		//
		// jsonResponse.add(ParamDefine.RESULT, StatusCode.SUCCESS.printStatus());
		// }
		// else {
		// jsonResponse.add(ParamDefine.RESULT, StatusCode.NO_CONTENT.printStatus("Cannot find item with ID " + idItem.getOriginalParam()));
		// }
		//
		// return jsonResponse.toString();
		// }
		// catch (Exception e) {
		// System.err.println(e.getMessage());
		// e.printStackTrace();
		// HibernateUtil.rollbackTransaction(session);
		// throw new ProcessException(e);
		// }
		JsonObject jsonResponse = new JsonObject();
		jsonResponse.add(ParamDefine.RESULT, StatusCode.DEPRECATED.printStatus());
		return jsonResponse.toString();
	}

	/**
	 * Calculate.
	 *
	 * @param idItem the id item
	 * @param quantity the quantity
	 * @return the string
	 */
	@GET
	@Path("/calculate")
	@Produces(value = { MediaType.APPLICATION_JSON })
	public String calculate(@QueryParam(ParamDefine.ITEM_ID) IntegerParam idItem, @QueryParam(ParamDefine.QUANTITY) FloatParam quantity) {
		// Session session = (Session) this.httpRequest.getAttribute(ParamDefine.HIBERNATE_SESSION);
		// try (ItemDAO itemDAO = AbstractDAO.borrowFromPool(DAOPool.itemPool)) {
		// JsonObject jsonResponse = new JsonObject();
		// ItemPOJO item = itemDAO.get(session, idItem.getValue());
		// if (item != null) {
		// float price = item.getPrice();
		// float amount = price * quantity.getValue().floatValue();
		// jsonResponse.addProperty(ParamDefine.AMOUNT, amount);
		// jsonResponse.add(ParamDefine.RESULT, StatusCode.SUCCESS.printStatus());
		// }
		// else {
		// jsonResponse.add(ParamDefine.RESULT, StatusCode.NO_CONTENT.printStatus());
		// }
		// item = null;
		//
		// return jsonResponse.toString();
		// }
		// catch (Exception e) {
		// System.err.println(e.getMessage());
		// e.printStackTrace();
		// throw new ProcessException(e);
		// }
		JsonObject jsonResponse = new JsonObject();
		jsonResponse.add(ParamDefine.RESULT, StatusCode.DEPRECATED.printStatus());
		return jsonResponse.toString();
	}

	/**
	 * List.
	 *
	 * @param page the page
	 * @return the string
	 */
	@GET
	@Path("/list")
	@Produces(value = { MediaType.APPLICATION_JSON })
	public String list(@QueryParam(ParamDefine.PAGE) IndexParam page) {
		if (page == null) {
			page = new IndexParam("1");
		}
		Session session = (Session) this.httpRequest.getAttribute(ParamDefine.HIBERNATE_SESSION);
		try (ItemDAO itemDAO = AbstractDAO.borrowFromPool(DAOPool.itemPool)) {
			JsonObject jsonResponse = new JsonObject();

			List<ItemPOJO> list = itemDAO.getList(session);
			Collections.sort(list, new Comparator<ItemPOJO>() {

				@Override
				public int compare(ItemPOJO o1, ItemPOJO o2) {
					return new CompareToBuilder().append(o2.isPrimary(), o1.isPrimary()).append(o2.getIdItem(), o1.getIdItem()).toComparison();
				}
			});

			if (list.size() > 0) {
				list = CollectionUtil.sortListByPage(list, page.getValue(), 10); // Default get 10 items per page
				JsonArray items = new JsonArray();
				for (ItemPOJO item : list) {
					JsonObject jsonItem = JsonUtil.toJsonObject(item);
					String photo = item.getPhoto();
					if (photo != null && !photo.isEmpty()) {
						String photoUrl = "http://" + this.httpRequest.getServerName() + ":" + this.httpRequest.getServerPort()
								+ this.httpRequest.getContextPath() + "/resource/item/" + item.getIdItem().toString() + "/" + photo;
						jsonItem.addProperty(ParamDefine.ITEM_PHOTO, photoUrl);
					}

					items.add(jsonItem);
				}
				jsonResponse.add(ParamDefine.ITEMS, items);
				jsonResponse.add(ParamDefine.RESULT, StatusCode.SUCCESS.printStatus());
			}
			else {
				jsonResponse.add(ParamDefine.RESULT, StatusCode.NO_CONTENT.printStatus());
			}
			list = null;

			return jsonResponse.toString();
		}
		catch (Exception e) {
			this.LOGGER.error(e.getMessage(), e);
			throw new ProcessException(e);
		}
	}

	/**
	 * Removes the favorite item.
	 *
	 * @param idItem the id item
	 * @return the string
	 */
	@POST
	@Path("/removefavorite")
	@Consumes(value = { MediaType.APPLICATION_FORM_URLENCODED })
	@Produces(value = { MediaType.APPLICATION_JSON })
	public String removeFavorite(@FormParam(ParamDefine.ITEM_ID) IntegerParam idItem) {
		// Session session = (Session) this.httpRequest.getAttribute(ParamDefine.HIBERNATE_SESSION);
		// try (ItemDAO itemDAO = AbstractDAO.borrowFromPool(DAOPool.itemPool); UserDAO userDAO = AbstractDAO.borrowFromPool(DAOPool.userPool)) {
		// JsonObject jsonResponse = new JsonObject();
		//
		// ItemPOJO item = itemDAO.get(session, idItem.getValue());
		// if (item != null) {
		// UserPOJO user = (UserPOJO) this.httpRequest.getAttribute(ParamDefine.USER);
		// user.removeLike(item);
		// userDAO.update(session, user);
		// HibernateUtil.commitTransaction(session);
		//
		// jsonResponse.add(ParamDefine.RESULT, StatusCode.SUCCESS.printStatus());
		// }
		// else {
		// jsonResponse.add(ParamDefine.RESULT, StatusCode.NO_CONTENT.printStatus("Cannot find item with ID " + idItem.getOriginalParam()));
		// }
		//
		// return jsonResponse.toString();
		// }
		// catch (Exception e) {
		// System.err.println(e.getMessage());
		// e.printStackTrace();
		// HibernateUtil.rollbackTransaction(session);
		// throw new ProcessException(e);
		// }
		JsonObject jsonResponse = new JsonObject();
		jsonResponse.add(ParamDefine.RESULT, StatusCode.DEPRECATED.printStatus());
		return jsonResponse.toString();
	}
}
