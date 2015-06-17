package com.haimosi.api.apps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
import logia.hibernate.util.HibernateUtil;
import logia.utility.json.JsonTool;

import org.hibernate.Session;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.haimosi.define.Constant;
import com.haimosi.define.StatusCode;
import com.haimosi.exception.ProcessException;
import com.haimosi.hibernate.dao.DAOPool;
import com.haimosi.hibernate.dao.ItemDAO;
import com.haimosi.hibernate.dao.RoleDAO;
import com.haimosi.hibernate.dao.TransactionDAO;
import com.haimosi.hibernate.pojo.ItemPOJO;
import com.haimosi.hibernate.pojo.RolePOJO;
import com.haimosi.hibernate.pojo.TransactionPOJO;
import com.haimosi.hibernate.pojo.UserPOJO;
import com.haimosi.param.ByteParam;
import com.haimosi.param.FloatParam;
import com.haimosi.param.IndexParam;
import com.haimosi.param.IntegerParam;
import com.haimosi.param.ParamDefine;
import com.haimosi.util.Helper;
import com.haimosi.websocket.data.MessagePushTransaction;
import com.haimosi.websocket.data.TransactionContent;
import com.haimosi.websocket.endpoint.WSEndpoint;

/**
 * The Class TransactionController_v1_0.
 * 
 * @author Paul Mai
 */
@Path("/api/v1.0/apps/transaction")
public class TransactionController_v1_0 {

	/** The http request. */
	@Context
	HttpServletRequest httpRequest;

	/**
	 * Creates the.
	 *
	 * @param idItem the id item
	 * @param quantity the quantity
	 * @param method the method
	 * @return the string
	 */
	@POST
	@Path("/create")
	@Consumes(value = { MediaType.APPLICATION_FORM_URLENCODED })
	@Produces(value = { MediaType.APPLICATION_JSON })
	public String create(@FormParam(ParamDefine.ITEM_ID) IntegerParam idItem, @FormParam(ParamDefine.QUANTITY) FloatParam quantity) {
		Session session = (Session) this.httpRequest.getAttribute(ParamDefine.HIBERNATE_SESSION);
		UserPOJO user = (UserPOJO) this.httpRequest.getAttribute(ParamDefine.USER);
		try (ItemDAO itemDAO = AbstractDAO.borrowFromPool(DAOPool.itemPool);
				TransactionDAO transDAO = AbstractDAO.borrowFromPool(DAOPool.transactionPool);
				RoleDAO roleDAO = AbstractDAO.borrowFromPool(DAOPool.rolePool)) {

			JsonObject jsonResponse = new JsonObject();
			ItemPOJO item = itemDAO.get(session, idItem.getValue());
			if (item != null) {
				float amount = quantity.getValue() * item.getPrice();
				Date date = new Date();
				TransactionPOJO trans = new TransactionPOJO(null, user, item, quantity.getValue(), amount, Constant.PAYMENT_UNCHOOSE, date,
						Constant.TRANS_WAIT, null);
				Integer id = transDAO.saveID(session, trans);
				HibernateUtil.commitTransaction(session);

				jsonResponse.add(ParamDefine.RESULT, StatusCode.SUCCESS.printStatus());

				// Push this transaction to admin through websocket
				MessagePushTransaction push = new MessagePushTransaction();
				push.setCommand(Constant.SOCKET_COMMAND_PUSH_TRANS);
				TransactionContent content = new TransactionContent(id, quantity.getValue(), amount, date);
				push.setContent(content);
				RolePOJO adminRole = roleDAO.get(session, Constant.USER_ROLE_ADMIN);
				Set<UserPOJO> admins = adminRole.getUsers();
				for (UserPOJO admin : admins) {
					WSEndpoint adminSocket = WSEndpoint._clientSessionMap.get(admin.getIdUser());
					if (adminSocket != null) {
						try {
							adminSocket.echoMessage(push);
						}
						catch (Exception e) {
							e.printStackTrace();
						}

					}
				}
				content = null;
				push = null;
				adminRole = null;
				admins = null;

			}
			else {
				jsonResponse.add(ParamDefine.RESULT, StatusCode.BAD_PARAM.printStatus());
			}

			return jsonResponse.toString();
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			HibernateUtil.rollbackTransaction(session);
			throw new ProcessException(e);
		}
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
	public String list(@QueryParam(ParamDefine.PAGE) IndexParam page, @QueryParam(ParamDefine.TYPE) ByteParam type) {
		if (page == null) {
			page = new IndexParam("1");
		}
		UserPOJO user = (UserPOJO) this.httpRequest.getAttribute(ParamDefine.USER);
		try {
			JsonObject jsonResponse = new JsonObject();

			@SuppressWarnings("unchecked")
			List<TransactionPOJO> list = (List<TransactionPOJO>) this.httpRequest.getSession().getAttribute(ParamDefine.TRANSACTIONS);
			if (page.getValue().equals(1) || list == null) {
				list = new ArrayList<TransactionPOJO>(user.getTransactions());
				switch (type.getValue()) {
					case Constant.TRANS_FILTER_FAVORITE:
						List<TransactionPOJO> tmpList = new ArrayList<TransactionPOJO>(list);
						list.clear();
						for (TransactionPOJO tmpTrans : tmpList) {
							if (tmpTrans.getItem().getLikes().contains(user)) {
								list.add(tmpTrans);
							}
						}
						break;

					case Constant.TRANS_FILTER_WEIGHT:
						Collections.sort(list, new Comparator<TransactionPOJO>() {

							@Override
							public int compare(TransactionPOJO o1, TransactionPOJO o2) {
								return (int) (o1.getQuantity() - o2.getQuantity());
							}
						});
						break;

					default: // Default filter by day
						Collections.sort(list, new Comparator<TransactionPOJO>() {

							@Override
							public int compare(TransactionPOJO o1, TransactionPOJO o2) {
								return o2.getTime().compareTo(o1.getTime());
							}
						});
						break;
				}

				this.httpRequest.getSession().setAttribute(ParamDefine.TRANSACTIONS, list);
			}

			if (list.size() > 0) {
				List<TransactionPOJO> tmpList = Helper.sortListByIndex(list, page.getValue(), 4); // Default get 4 items per page
				JsonArray transactions = new JsonArray();
				for (TransactionPOJO transaction : tmpList) {
					JsonObject jsonTransaction = JsonTool.toJsonObject(transaction);
					String photo = transaction.getPhoto();
					if (photo != null && !photo.isEmpty()) {
						String photoUrl = "http://" + this.httpRequest.getServerName() + ":" + this.httpRequest.getServerPort()
								+ this.httpRequest.getContextPath() + "/resource/transaction/" + photo;
						jsonTransaction.addProperty(ParamDefine.TRANSACTION_PHOTO, photoUrl);
					}
					ItemPOJO item = transaction.getItem();
					JsonObject jsonItem = JsonTool.toJsonObject(item);
					jsonItem.addProperty(ParamDefine.ITEM_STATUS_LIKE, item.isLike(user));
					jsonTransaction.add(ParamDefine.ITEM, jsonItem);

					transactions.add(jsonTransaction);
				}
				jsonResponse.add(ParamDefine.TRANSACTIONS, transactions);
				jsonResponse.add(ParamDefine.RESULT, StatusCode.SUCCESS.printStatus());

				tmpList = null;
			}
			else {
				jsonResponse.add(ParamDefine.RESULT, StatusCode.NO_CONTENT.printStatus());
			}
			list = null;

			return jsonResponse.toString();
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			throw new ProcessException(e);
		}
	}
}
