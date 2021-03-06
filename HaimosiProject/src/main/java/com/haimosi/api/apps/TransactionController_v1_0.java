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
import logia.utility.collection.CollectionUtil;
import logia.utility.json.JsonUtil;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.haimosi.api.util.CheckParameterInterface;
import com.haimosi.api.util.ImplCheckParameter;
import com.haimosi.define.Constant;
import com.haimosi.define.StatusCode;
import com.haimosi.exception.ProcessException;
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
import com.haimosi.pool.DAOPool;
import com.haimosi.runnable.CheckAcceptTransactionService;
import com.haimosi.util.Payment;
import com.haimosi.websocket.data.MessageAcceptTrans;
import com.haimosi.websocket.data.MessagePushTransaction;
import com.haimosi.websocket.data.TransactionContent;
import com.haimosi.websocket.endpoint.WSEndpoint;
import com.stripe.exception.CardException;
import com.stripe.model.Charge;

/**
 * The Class TransactionController_v1_0.
 * 
 * @author Paul Mai
 */
@Path("/api/v1.0/apps/transaction")
public class TransactionController_v1_0 {

	/** The logger. */
	private final Logger            LOGGER         = Logger.getLogger(this.getClass());

	/** The check parameter. */
	private CheckParameterInterface checkParameter = new ImplCheckParameter();

	/** The http request. */
	@Context
	HttpServletRequest              httpRequest;

	/**
	 * Accept the transaction.
	 *
	 * @param idTrans the id trans
	 * @param method the method
	 * @return the string
	 */
	@POST
	@Path("/accept")
	@Consumes(value = { MediaType.APPLICATION_FORM_URLENCODED })
	@Produces(value = { MediaType.APPLICATION_JSON })
	public String accept(@FormParam(ParamDefine.TRANSACTION_ID) IntegerParam idTrans, @FormParam(ParamDefine.METHOD) ByteParam method) {

		this.checkParameter.doCheck(idTrans, method);

		Session session = (Session) this.httpRequest.getAttribute(ParamDefine.HIBERNATE_SESSION);
		UserPOJO user = (UserPOJO) this.httpRequest.getAttribute(ParamDefine.USER);
		try (TransactionDAO transDAO = AbstractDAO.borrowFromPool(DAOPool.transactionPool);
		        RoleDAO roleDAO = AbstractDAO.borrowFromPool(DAOPool.rolePool)) {

			JsonObject jsonResponse = new JsonObject();

			TransactionPOJO trans = transDAO.get(session, idTrans.getValue());
			if (trans != null) {
				if (trans.getUser().equals(user)) {
					trans.setMethod(method.getValue());

					/* Call payment api if payment method in app, and set payment status */
					byte status;
					if (method.getValue().equals(Constant.PAYMENT_APP)) {
						// Payment must use Australian cents
						Payment payment = new Payment(user.getCreditAccount().getCardNumber(), user.getCreditAccount().getExpireDate(), user
						        .getCreditAccount().getCvvNumber(), (int) (trans.getAmount() * 100), "aud", "Pay for Haimosi's goods");
						try {
							Charge charge = payment.doPayment();
							if (charge.getStatus().equals("succeeded")) {

								// Update stripe charge ID to this transaction
								trans.setIdCharge(charge.getId());

								status = 1;

								jsonResponse.add(ParamDefine.RESULT, StatusCode.SUCCESS.printStatus());
							}
							else {
								status = 0;
							}
						}
						catch (CardException e) {
							this.LOGGER.error(e.getMessage(), e);
							status = 0;
							jsonResponse.add(ParamDefine.RESULT, StatusCode.INTERNAL_ERROR.printStatus(e.getMessage()));
						}
						catch (Exception e) {
							this.LOGGER.error(e.getMessage(), e);
							status = 0;
							jsonResponse.add(ParamDefine.RESULT, StatusCode.INTERNAL_ERROR.printStatus());
						}

					}
					else {
						status = 1; // Success when payment method is cash
						jsonResponse.add(ParamDefine.RESULT, StatusCode.SUCCESS.printStatus());
					}

					transDAO.update(session, trans);
					HibernateUtil.commitTransaction(session);

					/* Notify to administrator */
					MessageAcceptTrans message = new MessageAcceptTrans();
					message.setCommand(Constant.SOCKET_COMMAND_ACCEPT_TRANS);
					message.setTransid(idTrans.getValue());
					message.setMethod(method.getValue());
					message.setStatus(status);
					RolePOJO adminRole = roleDAO.get(session, Constant.USER_ROLE_ADMIN);
					Set<UserPOJO> admins = adminRole.getUsers();
					for (UserPOJO admin : admins) {
						WSEndpoint adminSocket = WSEndpoint._clientSessionMap.get(admin.getIdUser());
						if (adminSocket != null) {
							try {
								adminSocket.echoMessage(message);
							}
							catch (Exception e) {
								this.LOGGER.error(e.getMessage(), e);
							}

						}
					}
				}
				else {
					jsonResponse.add(ParamDefine.RESULT, StatusCode.BAD_REQUEST.printStatus("User not the owner of this transaction"));
				}
				trans = null;
			}
			else {
				jsonResponse.add(ParamDefine.RESULT,
				        StatusCode.NO_CONTENT.printStatus("Cannot find transaction with ID " + idTrans.getOriginalParam()));
			}

			return jsonResponse.toString();
		}
		catch (Exception e) {
			this.LOGGER.error(e.getMessage(), e);
			HibernateUtil.rollbackTransaction(session);
			throw new ProcessException(e);
		}
	}

	/**
	 * Adds the favorite.
	 *
	 * @param idTrans the id transaction
	 * @return the string
	 */
	@POST
	@Path("/addfavorite")
	@Consumes(value = { MediaType.APPLICATION_FORM_URLENCODED })
	@Produces(value = { MediaType.APPLICATION_JSON })
	public String addFavorite(@FormParam(ParamDefine.TRANSACTION_ID) IntegerParam idTrans) {

		this.checkParameter.doCheck(idTrans);

		Session session = (Session) this.httpRequest.getAttribute(ParamDefine.HIBERNATE_SESSION);
		try (TransactionDAO transDAO = AbstractDAO.borrowFromPool(DAOPool.transactionPool)) {
			JsonObject jsonResponse = new JsonObject();

			TransactionPOJO trans = transDAO.get(session, idTrans.getValue());
			if (trans != null) {
				trans.setLike(true);
				transDAO.update(session, trans);
				HibernateUtil.commitTransaction(session);

				jsonResponse.add(ParamDefine.RESULT, StatusCode.SUCCESS.printStatus());
			}
			else {
				jsonResponse.add(ParamDefine.RESULT,
				        StatusCode.NO_CONTENT.printStatus("Cannot find transaction with ID " + idTrans.getOriginalParam()));
			}

			return jsonResponse.toString();
		}
		catch (Exception e) {
			this.LOGGER.error(e.getMessage(), e);
			HibernateUtil.rollbackTransaction(session);
			throw new ProcessException(e);
		}
	}

	/**
	 * Creates the transaction.
	 *
	 * @param idItem the id item
	 * @param quantity the quantity
	 * @return the string
	 */
	@POST
	@Path("/create")
	@Consumes(value = { MediaType.APPLICATION_FORM_URLENCODED })
	@Produces(value = { MediaType.APPLICATION_JSON })
	public String create(@FormParam(ParamDefine.ITEM_ID) IntegerParam idItem, @FormParam(ParamDefine.QUANTITY) FloatParam quantity) {

		this.checkParameter.doCheck(idItem, quantity);

		Session session = (Session) this.httpRequest.getAttribute(ParamDefine.HIBERNATE_SESSION);
		UserPOJO user = (UserPOJO) this.httpRequest.getAttribute(ParamDefine.USER);
		try (ItemDAO itemDAO = AbstractDAO.borrowFromPool(DAOPool.itemPool);
		        TransactionDAO transDAO = AbstractDAO.borrowFromPool(DAOPool.transactionPool);
		        RoleDAO roleDAO = AbstractDAO.borrowFromPool(DAOPool.rolePool)) {

			JsonObject jsonResponse = new JsonObject();
			ItemPOJO item = itemDAO.get(session, idItem.getValue());
			if (item != null) {
				float amount = quantity.getValue() * (item.getPrice() / item.getBasicAmount());
				Date date = new Date();
				TransactionPOJO trans = new TransactionPOJO(null, user, item, quantity.getValue(), amount, Constant.PAYMENT_UNCHOOSE, date,
				        Constant.TRANS_STATUS_WAIT, null, false);
				Integer id = transDAO.saveID(session, trans);
				HibernateUtil.commitTransaction(session);

				jsonResponse.addProperty(ParamDefine.TRANSACTION_ID, id);
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
							this.LOGGER.error(e.getMessage(), e);
						}

					}
				}
				content = null;
				push = null;
				adminRole = null;
				admins = null;

				// Create thread check after 5 minute this transaction not accept by client, deny it
				CheckAcceptTransactionService service = new CheckAcceptTransactionService(id);
				new Thread(service).start();
			}
			else {
				jsonResponse.add(ParamDefine.RESULT, StatusCode.NO_CONTENT.printStatus("Cannot find item with ID " + idItem.getOriginalParam()));
			}

			return jsonResponse.toString();
		}
		catch (Exception e) {
			this.LOGGER.error(e.getMessage(), e);
			HibernateUtil.rollbackTransaction(session);
			throw new ProcessException(e);
		}
	}

	/**
	 * List of user's transactions.
	 *
	 * @param page the page
	 * @param type the type
	 * @return the string
	 */
	@GET
	@Path("/list")
	@Produces(value = { MediaType.APPLICATION_JSON })
	public String list(@QueryParam(ParamDefine.PAGE) IndexParam page, @QueryParam(ParamDefine.TYPE) ByteParam type) {
		if (page == null) {
			page = new IndexParam("1");
		}
		if (type == null) {
			type = new ByteParam("1");
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
							if (tmpTrans.isLike()) {
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
				List<TransactionPOJO> tmpList = CollectionUtil.sortListByPage(list, page.getValue(), 10); // Default get 10 items per page
				JsonArray transactions = new JsonArray();
				for (TransactionPOJO transaction : tmpList) {
					JsonObject jsonTransaction = JsonUtil.toJsonObject(transaction);
					String photo = transaction.getPhoto();
					if (photo != null && !photo.isEmpty()) {
						String photoUrl = "http://" + this.httpRequest.getServerName() + ":" + this.httpRequest.getServerPort()
						        + this.httpRequest.getContextPath() + "/resource/transaction/" + transaction.getIdTransaction().toString() + "/"
						        + photo;
						jsonTransaction.addProperty(ParamDefine.TRANSACTION_PHOTO, photoUrl);
					}
					ItemPOJO item = transaction.getItem();
					JsonObject jsonItem = JsonUtil.toJsonObject(item);
					String photoItem = item.getPhoto();
					if (photoItem != null && !photoItem.isEmpty()) {
						String photoUrl = "http://" + this.httpRequest.getServerName() + ":" + this.httpRequest.getServerPort()
						        + this.httpRequest.getContextPath() + "/resource/item/" + item.getIdItem().toString() + "/" + photoItem;
						jsonItem.addProperty(ParamDefine.ITEM_PHOTO, photoUrl);
					}
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
			this.LOGGER.error(e.getMessage(), e);
			throw new ProcessException(e);
		}
	}

	/**
	 * Removes the favorite.
	 *
	 * @param idTrans the id transaction
	 * @return the string
	 */
	@POST
	@Path("/removefavorite")
	@Consumes(value = { MediaType.APPLICATION_FORM_URLENCODED })
	@Produces(value = { MediaType.APPLICATION_JSON })
	public String removeFavorite(@FormParam(ParamDefine.TRANSACTION_ID) IntegerParam idTrans) {

		this.checkParameter.doCheck(idTrans);

		Session session = (Session) this.httpRequest.getAttribute(ParamDefine.HIBERNATE_SESSION);
		try (TransactionDAO transDAO = AbstractDAO.borrowFromPool(DAOPool.transactionPool)) {
			JsonObject jsonResponse = new JsonObject();

			TransactionPOJO trans = transDAO.get(session, idTrans.getValue());
			if (trans != null) {
				trans.setLike(false);
				transDAO.update(session, trans);
				HibernateUtil.commitTransaction(session);

				jsonResponse.add(ParamDefine.RESULT, StatusCode.SUCCESS.printStatus());
			}
			else {
				jsonResponse.add(ParamDefine.RESULT,
				        StatusCode.NO_CONTENT.printStatus("Cannot find transaction with ID " + idTrans.getOriginalParam()));
			}

			return jsonResponse.toString();
		}
		catch (Exception e) {
			this.LOGGER.error(e.getMessage(), e);
			HibernateUtil.rollbackTransaction(session);
			throw new ProcessException(e);
		}
	}

}
