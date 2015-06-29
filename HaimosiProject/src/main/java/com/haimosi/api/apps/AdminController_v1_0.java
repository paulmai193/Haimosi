package com.haimosi.api.apps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import logia.utility.filechecker.ImageChecker;
import logia.utility.image.ScaleImage;
import logia.utility.json.JsonTool;

import org.apache.commons.io.IOUtils;
import org.hibernate.Session;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.haimosi.define.Config;
import com.haimosi.define.Constant;
import com.haimosi.define.StatusCode;
import com.haimosi.exception.BadParamException;
import com.haimosi.exception.ProcessException;
import com.haimosi.hibernate.dao.CreditAccountDAO;
import com.haimosi.hibernate.dao.ItemDAO;
import com.haimosi.hibernate.dao.LightDAO;
import com.haimosi.hibernate.dao.ScaleDAO;
import com.haimosi.hibernate.dao.TransactionDAO;
import com.haimosi.hibernate.dao.UserDAO;
import com.haimosi.hibernate.pojo.CreditAccountPOJO;
import com.haimosi.hibernate.pojo.ItemPOJO;
import com.haimosi.hibernate.pojo.LightPOJO;
import com.haimosi.hibernate.pojo.ScalePOJO;
import com.haimosi.hibernate.pojo.TransactionPOJO;
import com.haimosi.hibernate.pojo.UserPOJO;
import com.haimosi.param.BooleanParam;
import com.haimosi.param.ByteParam;
import com.haimosi.param.ContactParam;
import com.haimosi.param.CreditExpireParam;
import com.haimosi.param.FloatParam;
import com.haimosi.param.IndexParam;
import com.haimosi.param.IntegerParam;
import com.haimosi.param.ParamDefine;
import com.haimosi.param.StringNotEmptyParam;
import com.haimosi.pool.DAOPool;
import com.haimosi.util.Helper;
import com.haimosi.websocket.data.MessageConfirmAcceptTrans;
import com.haimosi.websocket.data.MessageConfirmTransaction;
import com.haimosi.websocket.data.MessageUpdateItem;
import com.haimosi.websocket.data.TransConfirmContent;
import com.haimosi.websocket.endpoint.WSEndpoint;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;

/**
 * The Class AdminController_v1_0.
 */
@Path("/api/v1.0/apps/admin")
public class AdminController_v1_0 {

	/** The http request. */
	@Context
	HttpServletRequest httpRequest;

	/**
	 * Accept transaction.
	 *
	 * @param id the id
	 * @param status the status
	 * @return the string
	 */
	@POST
	@Path("/accepttransaction")
	public String acceptTransaction(@FormParam(ParamDefine.TRANSACTION_ID) IntegerParam id, @FormParam(ParamDefine.STATUS) ByteParam status) {
		Session session = (Session) this.httpRequest.getAttribute(ParamDefine.HIBERNATE_SESSION);

		try (TransactionDAO transDAO = AbstractDAO.borrowFromPool(DAOPool.transactionPool)) {
			JsonObject jsonResponse = new JsonObject();

			TransactionPOJO trans = transDAO.get(session, id.getValue());
			if (trans != null) {
				if (trans.getStatus() == Constant.TRANS_WAIT) {
					if (status.getValue().equals(1)) {
						trans.setStatus(Constant.TRANS_DONE);
					}
					else {
						trans.setStatus(Constant.TRANS_DENY);
					}

					transDAO.update(session, trans);
					HibernateUtil.commitTransaction(session);

					// Send push notify accepted to user
					MessageConfirmAcceptTrans message = new MessageConfirmAcceptTrans();
					message.setCommand(Constant.SOCKET_COMMAND_CONFIRM_ACCEPT_TRANS);
					message.setTransid(id.getValue());
					message.setStatus(status.getValue());
					WSEndpoint userSocket = WSEndpoint._clientSessionMap.get(trans.getUser().getIdUser());
					if (userSocket != null) {
						try {
							userSocket.echoMessage(message);
						}
						catch (Exception e) {
							e.printStackTrace();
						}
					}
					message = null;
				}
				else {
					jsonResponse.add(ParamDefine.RESULT,
					        StatusCode.NO_CONTENT.printStatus("Transaction ID " + id.getOriginalParam() + " payment was accepted before"));
				}
			}
			else {
				jsonResponse.add(ParamDefine.RESULT, StatusCode.NO_CONTENT.printStatus("Cannot find transaction with ID " + id.getOriginalParam()));
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
	 * Adds the item.
	 *
	 * @param formData the form data
	 * @return the string
	 */
	@POST
	@Path("/additem")
	@Consumes(value = { MediaType.MULTIPART_FORM_DATA })
	@Produces(value = { MediaType.APPLICATION_JSON })
	public String addItem(FormDataMultiPart formData) {
		Session session = (Session) this.httpRequest.getAttribute(ParamDefine.HIBERNATE_SESSION);

		FormDataBodyPart namePart = formData.getField(ParamDefine.ITEM_NAME);
		String itemName = namePart.getValue();

		FormDataBodyPart pricePart = formData.getField(ParamDefine.ITEM_PRICE);
		Float itemPrice = Float.valueOf(pricePart.getValue());

		FormDataBodyPart unitPart = formData.getField(ParamDefine.ITEM_UNIT);
		String itemUnit = unitPart.getValue();

		FormDataBodyPart descPart = formData.getField(ParamDefine.ITEM_DESCRIPTION);
		String itemDesc = descPart.getValue();

		FormDataBodyPart fileBody = formData.getField(ParamDefine.ITEM_PHOTO);
		String fileMineType;
		try {
			String fileName = fileBody.getContentDisposition().getFileName();
			fileMineType = fileName.substring(fileName.lastIndexOf("."));
		}
		catch (Exception e) {
			fileMineType = ".png";
		}

		FormDataBodyPart amountPart = formData.getField(ParamDefine.ITEM_BASIC_AMOUNT);
		Float itemAmount = Float.valueOf(amountPart.getValue());

		FormDataBodyPart isPrimaryPath = formData.getField(ParamDefine.IS_PRIMARY);
		Boolean isPrimary = Boolean.valueOf(isPrimaryPath.getValue());

		// Precheck required parameter
		if (itemName == null || itemName.trim().isEmpty()) {
			throw new BadParamException(new Throwable("Invalid item name"));
		}
		if (itemPrice == null) {
			throw new BadParamException(new Throwable("Invalid item price"));
		}
		if (itemUnit == null || itemUnit.trim().isEmpty()) {
			throw new BadParamException(new Throwable("Invalid item unit"));
		}
		if (isPrimary == null) {
			isPrimary = true;
		}

		try (InputStream fileStream = fileBody.getValueAs(InputStream.class);
		        ItemDAO itemDAO = AbstractDAO.borrowFromPool(DAOPool.itemPool);
		        UserDAO userDAO = AbstractDAO.borrowFromPool(DAOPool.userPool)) {
			JsonObject jsonResponse = new JsonObject();

			// Create item record
			ItemPOJO item = new ItemPOJO(null, itemName, itemDesc, itemPrice, itemUnit, null, itemAmount, isPrimary);
			Integer idItem = itemDAO.saveID(session, item);
			if (idItem != null) {
				item.setIdItem(idItem);

				// Get item photo (if exist)
				byte[] bytes = IOUtils.toByteArray(fileStream);
				final String PREFIX = "stream2file";
				final String SUFFIX = ".tmp";
				File tempFile = File.createTempFile(PREFIX, SUFFIX);

				try (OutputStream fOut = new FileOutputStream(tempFile)) {
					fOut.write(bytes);
					fOut.flush();

					// Check file
					if (ImageChecker.isImage(tempFile)) {
						File directoryTrans = new File(Config.resource_item_path + idItem.toString());
						if (!directoryTrans.exists()) {
							directoryTrans.mkdir();
						}
						String namePhoto = System.currentTimeMillis() + fileMineType;
						File filePhoto = new File(directoryTrans.getAbsolutePath() + Constant.SEPERATOR + namePhoto);
						ScaleImage.doScale(tempFile, filePhoto, 320);

						// Write photo success, update item in database
						item.setPhoto(namePhoto);
						itemDAO.saveOrUpdate(session, item);

						// Update new item for all user
						MessageUpdateItem message = new MessageUpdateItem();
						message.setCommand(Constant.SOCKET_COMMAND_UPDATE_ITEM);
						for (UserPOJO tmpUser : userDAO.getList(session)) {
							WSEndpoint userSocket = WSEndpoint._clientSessionMap.get(tmpUser.getIdUser());
							if (userSocket != null) {
								try {
									userSocket.echoMessage(message);
								}
								catch (Exception e) {
									e.printStackTrace();
								}
							}
						}

						jsonResponse.add(ParamDefine.RESULT, StatusCode.SUCCESS.printStatus());

						directoryTrans = null;
						filePhoto = null;
						namePhoto = null;

					}
					else {
						jsonResponse.add(ParamDefine.RESULT, StatusCode.BAD_REQUEST.printStatus("File uploaded not image type"));
					}
					HibernateUtil.commitTransaction(session);
				}
				catch (Exception e) {
					System.err.println(e.getMessage());
					e.printStackTrace();
					HibernateUtil.rollbackTransaction(session);
					throw new ProcessException(e);
				}
				finally {
					bytes = null;
					tempFile.delete();
				}
			}
			else {
				HibernateUtil.rollbackTransaction(session);
				jsonResponse.add(ParamDefine.RESULT, StatusCode.INTERNAL_ERROR.printStatus("Cannot create new item"));
			}

			return jsonResponse.toString();
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			throw new ProcessException(e);
		}
		finally {
			fileBody.cleanup();
			fileBody = null;
			fileMineType = null;
			namePart.cleanup();
			namePart = null;
			pricePart.cleanup();
			pricePart = null;
			unitPart.cleanup();
			unitPart = null;
			descPart.cleanup();
			descPart = null;
			amountPart.cleanup();
			amountPart = null;
		}
	}

	/**
	 * Adds the light.
	 *
	 * @param port the port
	 * @param color the color
	 * @return the string
	 */
	@POST
	@Path("/addlight")
	@Consumes(value = { MediaType.APPLICATION_FORM_URLENCODED })
	@Produces(value = { MediaType.APPLICATION_JSON })
	public String addLight(@FormParam(ParamDefine.LIGHT_PORT) IntegerParam port, @FormParam(ParamDefine.LIGHT_COLOR) String color) {
		Session session = (Session) this.httpRequest.getAttribute(ParamDefine.HIBERNATE_SESSION);

		try (LightDAO lightDAO = AbstractDAO.borrowFromPool(DAOPool.lightPool)) {
			JsonObject jsonResponse = new JsonObject();

			LightPOJO light = new LightPOJO(null, color, port.getValue());
			lightDAO.saveOrUpdate(session, light);
			HibernateUtil.commitTransaction(session);
			jsonResponse.add(ParamDefine.RESULT, StatusCode.SUCCESS.printStatus());

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
	 * Adds the scale.
	 *
	 * @param metta the metta
	 * @param parameter the parameter
	 * @param position the position
	 * @param specification the specification
	 * @return the string
	 */
	@POST
	@Path("/addscale")
	@Consumes(value = { MediaType.APPLICATION_FORM_URLENCODED })
	@Produces(value = { MediaType.APPLICATION_JSON })
	public String addScale(@FormParam(ParamDefine.SCALE_METTA) String metta, @FormParam(ParamDefine.SCALE_PARAMETER) String parameter,
	        @FormParam(ParamDefine.SCALE_POSITION) String position, @FormParam(ParamDefine.SCALE_SPECIFICATION) String specification) {
		Session session = (Session) this.httpRequest.getAttribute(ParamDefine.HIBERNATE_SESSION);

		try (ScaleDAO scaleDAO = AbstractDAO.borrowFromPool(DAOPool.scalePool)) {
			JsonObject jsonResponse = new JsonObject();

			ScalePOJO scale = new ScalePOJO(null, specification, parameter, position, metta);
			scaleDAO.saveOrUpdate(session, scale);
			HibernateUtil.commitTransaction(session);
			jsonResponse.add(ParamDefine.RESULT, StatusCode.SUCCESS.printStatus());

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
	 * Change photo item.
	 *
	 * @param formData the form data
	 * @return the string
	 */
	@POST
	@Path("/changephotoitem")
	@Consumes(value = { MediaType.MULTIPART_FORM_DATA })
	@Produces(value = { MediaType.APPLICATION_JSON })
	public String changePhotoItem(FormDataMultiPart formData) {
		Session session = (Session) this.httpRequest.getAttribute(ParamDefine.HIBERNATE_SESSION);
		FormDataBodyPart fileBody = formData.getField(ParamDefine.ITEM_PHOTO);

		FormDataBodyPart idPart = formData.getField(ParamDefine.ITEM_ID);
		Integer idItem = Integer.valueOf(idPart.getValue());

		String fileMineType;
		try {
			String fileName = fileBody.getContentDisposition().getFileName();
			fileMineType = fileName.substring(fileName.lastIndexOf("."));
		}
		catch (Exception e) {
			fileMineType = ".png";
		}

		try (InputStream fileStream = fileBody.getValueAs(InputStream.class);
		        ItemDAO itemDAO = AbstractDAO.borrowFromPool(DAOPool.itemPool);
		        UserDAO userDAO = AbstractDAO.borrowFromPool(DAOPool.userPool)) {

			JsonObject jsonResponse = new JsonObject();

			ItemPOJO item = itemDAO.get(session, idItem);
			if (item != null) {
				byte[] bytes = IOUtils.toByteArray(fileStream);
				final String PREFIX = "stream2file";
				final String SUFFIX = ".tmp";
				File tempFile = File.createTempFile(PREFIX, SUFFIX);

				try (OutputStream fOut = new FileOutputStream(tempFile)) {
					fOut.write(bytes);
					fOut.flush();

					// Check file
					if (ImageChecker.isImage(tempFile)) {
						File directoryItem = new File(Config.resource_item_path + idItem.toString());
						if (!directoryItem.exists()) {
							directoryItem.mkdir();
						}
						String namePhoto = System.currentTimeMillis() + fileMineType;
						File filePhoto = new File(directoryItem.getAbsolutePath() + Constant.SEPERATOR + namePhoto);
						ScaleImage.doScale(tempFile, filePhoto, 320);

						// Write avatar success, update user in database
						item.setPhoto(namePhoto);
						itemDAO.saveOrUpdate(session, item);
						HibernateUtil.commitTransaction(session);

						// Update new item for all user
						MessageUpdateItem message = new MessageUpdateItem();
						message.setCommand(Constant.SOCKET_COMMAND_UPDATE_ITEM);
						for (UserPOJO tmpUser : userDAO.getList(session)) {
							WSEndpoint userSocket = WSEndpoint._clientSessionMap.get(tmpUser.getIdUser());
							if (userSocket != null) {
								try {
									userSocket.echoMessage(message);
								}
								catch (Exception e) {
									e.printStackTrace();
								}
							}
						}

						jsonResponse.add(ParamDefine.RESULT, StatusCode.SUCCESS.printStatus());

						directoryItem = null;
						filePhoto = null;
						namePhoto = null;
					}
					else {
						jsonResponse.add(ParamDefine.RESULT, StatusCode.BAD_REQUEST.printStatus("File upload not image type"));
					}

				}
				catch (Exception e) {
					System.err.println(e.getMessage());
					e.printStackTrace();
					HibernateUtil.rollbackTransaction(session);
					throw new ProcessException(e);
				}
				finally {
					bytes = null;
					tempFile.delete();
				}
			}
			else {
				jsonResponse.add(ParamDefine.RESULT, StatusCode.NO_CONTENT.printStatus("Cannot find item with ID " + idItem.toString()));
			}

			return jsonResponse.toString();
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			throw new ProcessException(e);
		}
		finally {
			fileBody.cleanup();
			fileBody = null;
			fileMineType = null;
		}
	}

	/**
	 * Change user status.
	 *
	 * @param idUser the id user
	 * @param status the status
	 * @return the string
	 */
	@POST
	@Path("/changeuserstatus")
	@Consumes(value = { MediaType.APPLICATION_FORM_URLENCODED })
	@Produces(value = { MediaType.APPLICATION_JSON })
	public String changeUserStatus(@FormParam(ParamDefine.USER_ID) IntegerParam idUser, @FormParam(ParamDefine.STATUS) ByteParam status) {
		Session session = (Session) this.httpRequest.getAttribute(ParamDefine.HIBERNATE_SESSION);
		if (status == null || !status.getValue().equals(Constant.USER_STATUS_ACTIVATE) || !status.getValue().equals(Constant.USER_STATUS_INACTIVATE)
		        || !status.getValue().equals(Constant.USER_STATUS_LOCKED)) {
			throw new BadParamException(new Throwable("Invalid status value"));
		}
		try (UserDAO userDAO = AbstractDAO.borrowFromPool(DAOPool.userPool)) {

			JsonObject jsonResponse = new JsonObject();

			UserPOJO user = userDAO.get(session, idUser.getValue());
			if (user != null) {
				user.setStatus(status.getValue());
				userDAO.update(session, user);
				HibernateUtil.commitTransaction(session);
				jsonResponse.add(ParamDefine.RESULT, StatusCode.SUCCESS.printStatus());

				user = null;
			}
			else {
				jsonResponse.add(ParamDefine.RESULT, StatusCode.NO_CONTENT.printStatus("Cannot find user with ID " + idUser.getOriginalParam()));
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
	 * Delete item.
	 *
	 * @param id the id
	 * @return the string
	 */
	@DELETE
	@Path("/deleteitem")
	@Produces(value = { MediaType.APPLICATION_JSON })
	public String deleteItem(@QueryParam(ParamDefine.ITEM_ID) IntegerParam id) {
		Session session = (Session) this.httpRequest.getAttribute(ParamDefine.HIBERNATE_SESSION);

		try (ItemDAO itemDAO = AbstractDAO.borrowFromPool(DAOPool.itemPool); UserDAO userDAO = AbstractDAO.borrowFromPool(DAOPool.userPool)) {
			JsonObject jsonResponse = new JsonObject();

			ItemPOJO item = itemDAO.get(session, id.getValue());
			if (item != null) {
				itemDAO.delete(session, item);
				HibernateUtil.commitTransaction(session);

				// Update new item for all user
				MessageUpdateItem message = new MessageUpdateItem();
				message.setCommand(Constant.SOCKET_COMMAND_UPDATE_ITEM);
				for (UserPOJO tmpUser : userDAO.getList(session)) {
					WSEndpoint userSocket = WSEndpoint._clientSessionMap.get(tmpUser.getIdUser());
					if (userSocket != null) {
						try {
							userSocket.echoMessage(message);
						}
						catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

				jsonResponse.add(ParamDefine.RESULT, StatusCode.SUCCESS.printStatus());
			}
			else {
				jsonResponse.add(ParamDefine.RESULT, StatusCode.NO_CONTENT.printStatus("Cannot find item with ID " + id.getOriginalParam()));
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
	 * Delete light.
	 *
	 * @param id the id
	 * @return the string
	 */
	@DELETE
	@Path("/deletelight")
	@Produces(value = { MediaType.APPLICATION_JSON })
	public String deleteLight(@QueryParam(ParamDefine.LIGHT_ID) IntegerParam id) {
		Session session = (Session) this.httpRequest.getAttribute(ParamDefine.HIBERNATE_SESSION);

		try (LightDAO lightDAO = AbstractDAO.borrowFromPool(DAOPool.lightPool)) {
			JsonObject jsonResponse = new JsonObject();

			LightPOJO light = lightDAO.get(session, id.getValue());
			if (light != null) {
				lightDAO.delete(session, light);
				HibernateUtil.commitTransaction(session);
				jsonResponse.add(ParamDefine.RESULT, StatusCode.SUCCESS.printStatus());
			}
			else {
				jsonResponse.add(ParamDefine.RESULT, StatusCode.NO_CONTENT.printStatus("Cannot find light with ID " + id.getOriginalParam()));
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
	 * Delete scale.
	 *
	 * @param id the id
	 * @return the string
	 */
	@DELETE
	@Path("/deletescale")
	@Produces(value = { MediaType.APPLICATION_JSON })
	public String deleteScale(@QueryParam(ParamDefine.SCALE_ID) IntegerParam id) {
		Session session = (Session) this.httpRequest.getAttribute(ParamDefine.HIBERNATE_SESSION);

		try (ScaleDAO scaleDAO = AbstractDAO.borrowFromPool(DAOPool.scalePool)) {
			JsonObject jsonResponse = new JsonObject();

			ScalePOJO scale = scaleDAO.get(session, id.getValue());
			if (scale != null) {
				scaleDAO.delete(session, scale);
				HibernateUtil.commitTransaction(session);
				jsonResponse.add(ParamDefine.RESULT, StatusCode.SUCCESS.printStatus());
			}
			else {
				jsonResponse.add(ParamDefine.RESULT, StatusCode.NO_CONTENT.printStatus("Cannot find scale with ID " + id.getOriginalParam()));
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
	 * Delete user.
	 *
	 * @param id the id
	 * @return the string
	 */
	@DELETE
	@Path("/deleteuser")
	@Produces(value = { MediaType.APPLICATION_JSON })
	public String deleteUser(@QueryParam(ParamDefine.USER_ID) IntegerParam id) {
		Session session = (Session) this.httpRequest.getAttribute(ParamDefine.HIBERNATE_SESSION);

		try (UserDAO userDAO = AbstractDAO.borrowFromPool(DAOPool.userPool)) {
			JsonObject jsonResponse = new JsonObject();

			UserPOJO user = userDAO.get(session, id.getValue());
			if (user != null) {
				userDAO.delete(session, user);
				HibernateUtil.commitTransaction(session);
				jsonResponse.add(ParamDefine.RESULT, StatusCode.SUCCESS.printStatus());
			}
			else {
				jsonResponse.add(ParamDefine.RESULT, StatusCode.NO_CONTENT.printStatus("Cannot find user with ID " + id.getOriginalParam()));
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
	 * Edits the account.
	 *
	 * @param idCard the id card
	 * @param cardName the card name
	 * @param cardNumber the card number
	 * @param cvvNumber the cvv number
	 * @param expireDay the expire day
	 * @return the string
	 */
	@POST
	@Path("/editaccount")
	@Consumes(value = { MediaType.APPLICATION_FORM_URLENCODED })
	@Produces(value = { MediaType.APPLICATION_JSON })
	public String editAccount(@FormParam(ParamDefine.CARD_ID) IntegerParam idCard, @FormParam(ParamDefine.CARD_NAME) StringNotEmptyParam cardName,
	        @FormParam(ParamDefine.CARD_NUMBER) StringNotEmptyParam cardNumber, @FormParam(ParamDefine.CVV_NUMBER) StringNotEmptyParam cvvNumber,
	        @FormParam(ParamDefine.EXPIRE) CreditExpireParam expireDay) {
		Session session = (Session) this.httpRequest.getAttribute(ParamDefine.HIBERNATE_SESSION);
		try (CreditAccountDAO creditDAO = AbstractDAO.borrowFromPool(DAOPool.creditPool)) {
			JsonObject jsonResponse = new JsonObject();

			CreditAccountPOJO credit = creditDAO.get(session, idCard.getValue());
			if (credit != null) {
				if (cardName != null) {
					credit.setCardName(cardName.getValue());
				}
				if (cardNumber != null) {
					credit.setCardNumber(cardNumber.getValue());
				}
				if (cvvNumber != null) {
					credit.setCvvNumber(cvvNumber.getValue());
				}
				if (expireDay != null) {
					credit.setExpireDate(expireDay.getValue());
				}
				creditDAO.saveOrUpdate(session, credit);
				HibernateUtil.commitTransaction(session);

				credit = null;

				jsonResponse.add(ParamDefine.RESULT, StatusCode.SUCCESS.printStatus());
			}
			else {
				jsonResponse.add(ParamDefine.RESULT,
				        StatusCode.NO_CONTENT.printStatus("Cannot find credit account with ID " + idCard.getOriginalParam()));
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
	 * Edits the item.
	 *
	 * @param idItem the id item
	 * @param desc the desc
	 * @param name the name
	 * @param price the price
	 * @param unit the unit
	 * @param basicAmount the basic amount
	 * @return the string
	 */
	@POST
	@Path("/edititem")
	@Consumes(value = { MediaType.APPLICATION_FORM_URLENCODED })
	@Produces(value = { MediaType.APPLICATION_JSON })
	public String editItem(@FormParam(ParamDefine.ITEM_ID) IntegerParam idItem, @FormParam(ParamDefine.ITEM_DESCRIPTION) String desc,
	        @FormParam(ParamDefine.ITEM_NAME) StringNotEmptyParam name, @FormParam(ParamDefine.ITEM_PRICE) FloatParam price,
	        @FormParam(ParamDefine.ITEM_UNIT) StringNotEmptyParam unit, @FormParam(ParamDefine.ITEM_BASIC_AMOUNT) FloatParam basicAmount,
	        @FormParam(ParamDefine.IS_PRIMARY) BooleanParam isPrimary) {
		Session session = (Session) this.httpRequest.getAttribute(ParamDefine.HIBERNATE_SESSION);
		try (ItemDAO itemDAO = AbstractDAO.borrowFromPool(DAOPool.itemPool); UserDAO userDAO = AbstractDAO.borrowFromPool(DAOPool.userPool)) {

			JsonObject jsonResponse = new JsonObject();

			ItemPOJO item = itemDAO.get(session, idItem.getValue());
			if (item != null) {
				if (desc != null) {
					item.setDescription(desc);
				}
				if (name != null) {
					item.setName(name.getValue());
				}
				if (price != null) {
					item.setPrice(price.getValue());
				}
				if (unit != null) {
					item.setUnit(unit.getValue());
				}
				if (basicAmount != null) {
					item.setBasicAmount(basicAmount.getValue());
				}
				if (isPrimary != null) {
					item.setPrimary(isPrimary.getValue());
				}
				itemDAO.saveOrUpdate(session, item);
				HibernateUtil.commitTransaction(session);

				item = null;

				// Update new item for all user
				MessageUpdateItem message = new MessageUpdateItem();
				message.setCommand(Constant.SOCKET_COMMAND_UPDATE_ITEM);
				for (UserPOJO tmpUser : userDAO.getList(session)) {
					WSEndpoint userSocket = WSEndpoint._clientSessionMap.get(tmpUser.getIdUser());
					if (userSocket != null) {
						try {
							userSocket.echoMessage(message);
						}
						catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

				jsonResponse.add(ParamDefine.RESULT, StatusCode.SUCCESS.printStatus());
			}
			else {
				jsonResponse.add(ParamDefine.RESULT, StatusCode.NO_CONTENT.printStatus("Cannot find item with ID " + idItem.getOriginalParam()));
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
	 * Edits the light.
	 *
	 * @param id the id
	 * @param port the port
	 * @param color the color
	 * @return the string
	 */
	@POST
	@Path("/editlight")
	@Consumes(value = { MediaType.APPLICATION_FORM_URLENCODED })
	@Produces(value = { MediaType.APPLICATION_JSON })
	public String editLight(@FormParam(ParamDefine.LIGHT_ID) IntegerParam id, @FormParam(ParamDefine.LIGHT_PORT) IntegerParam port,
	        @FormParam(ParamDefine.LIGHT_COLOR) String color) {
		Session session = (Session) this.httpRequest.getAttribute(ParamDefine.HIBERNATE_SESSION);

		try (LightDAO lightDAO = AbstractDAO.borrowFromPool(DAOPool.lightPool)) {
			JsonObject jsonResponse = new JsonObject();

			LightPOJO light = lightDAO.get(session, id.getValue());
			if (light != null) {
				if (port != null) {
					light.setPort(port.getValue());
				}
				if (color != null) {
					light.setColor(color);
				}
				lightDAO.update(session, light);
				HibernateUtil.commitTransaction(session);
				jsonResponse.add(ParamDefine.RESULT, StatusCode.SUCCESS.printStatus());
			}
			else {
				jsonResponse.add(ParamDefine.RESULT, StatusCode.NO_CONTENT.printStatus("Cannot find light with ID " + id.getOriginalParam()));
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
	 * Edits the profile.
	 *
	 * @param idUser the id user
	 * @param firstName the first name
	 * @param lastName the last name
	 * @param phone the phone
	 * @param email the email
	 * @return the string
	 */
	@POST
	@Path("/edituser")
	@Consumes(value = { MediaType.APPLICATION_FORM_URLENCODED })
	@Produces(value = { MediaType.APPLICATION_JSON })
	public String editProfile(@FormParam(ParamDefine.USER_ID) IntegerParam idUser, @FormParam(ParamDefine.FIRST_NAME) StringNotEmptyParam firstName,
	        @FormParam(ParamDefine.LAST_NAME) StringNotEmptyParam lastName, @FormParam(ParamDefine.PHONE) ContactParam phone,
	        @FormParam(ParamDefine.EMAIL) ContactParam email) {
		Session session = (Session) this.httpRequest.getAttribute(ParamDefine.HIBERNATE_SESSION);
		try (UserDAO userDAO = AbstractDAO.borrowFromPool(DAOPool.userPool)) {
			JsonObject jsonResponse = new JsonObject();

			UserPOJO user = userDAO.get(session, idUser.getValue());
			if (user != null) {
				if (firstName != null) {
					user.setFirstName(firstName.getValue());
				}
				if (lastName != null) {
					user.setLastName(lastName.getValue());
				}
				if (phone != null) {
					if (phone.getContactType() == ContactParam.CONTACT_PHONE) {
						user.setPhone(phone.getValue());
					}
					else {
						throw new BadParamException();
					}
				}
				if (email != null) {
					if (email.getContactType() == ContactParam.CONTACT_EMAIL) {
						user.setEmail(email.getValue());
					}
					else {
						throw new BadParamException();
					}
				}
				userDAO.saveOrUpdate(session, user);
				HibernateUtil.commitTransaction(session);
				jsonResponse.add(ParamDefine.RESULT, StatusCode.SUCCESS.printStatus());
			}
			else {
				jsonResponse.add(ParamDefine.RESULT, StatusCode.NO_CONTENT.printStatus("Cannot find user with ID " + idUser.getOriginalParam()));
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
	 * Edits the scale.
	 *
	 * @param id the id
	 * @param metta the metta
	 * @param parameter the parameter
	 * @param position the position
	 * @param specification the specification
	 * @return the string
	 */
	@POST
	@Path("/editscale")
	@Consumes(value = { MediaType.APPLICATION_FORM_URLENCODED })
	@Produces(value = { MediaType.APPLICATION_JSON })
	public String editScale(@FormParam(ParamDefine.SCALE_ID) IntegerParam id, @FormParam(ParamDefine.SCALE_METTA) String metta,
	        @FormParam(ParamDefine.SCALE_PARAMETER) String parameter, @FormParam(ParamDefine.SCALE_POSITION) String position,
	        @FormParam(ParamDefine.SCALE_SPECIFICATION) String specification) {
		Session session = (Session) this.httpRequest.getAttribute(ParamDefine.HIBERNATE_SESSION);

		try (ScaleDAO scaleDAO = AbstractDAO.borrowFromPool(DAOPool.scalePool)) {
			JsonObject jsonResponse = new JsonObject();

			ScalePOJO scale = scaleDAO.get(session, id.getValue());
			if (scale != null) {
				if (metta != null) {
					scale.setMetta(metta);
				}
				if (parameter != null) {
					scale.setParameter(parameter);
				}
				if (position != null) {
					scale.setPosition(position);
				}
				if (specification != null) {
					scale.setSpecification(specification);
				}
				scaleDAO.update(session, scale);
				HibernateUtil.commitTransaction(session);
				jsonResponse.add(ParamDefine.RESULT, StatusCode.SUCCESS.printStatus());
			}
			else {
				jsonResponse.add(ParamDefine.RESULT, StatusCode.NO_CONTENT.printStatus("Cannot find scale with ID " + id.getOriginalParam()));
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
	 * List device.
	 *
	 * @return the string
	 */
	@GET
	@Path(value = "/listdevice")
	@Produces(value = { MediaType.APPLICATION_JSON })
	public String listDevice() {
		Session session = (Session) this.httpRequest.getAttribute(ParamDefine.HIBERNATE_SESSION);

		try (ScaleDAO scaleDAO = AbstractDAO.borrowFromPool(DAOPool.scalePool); LightDAO lightDAO = AbstractDAO.borrowFromPool(DAOPool.lightPool)) {
			JsonObject jsonResponse = new JsonObject();
			// JsonArray array = new JsonArray();
			List<ScalePOJO> scales = scaleDAO.getList(session);
			// for (ScalePOJO scale : scales) {
			// array.add(scale.toJson());
			// }
			jsonResponse.add(ParamDefine.SCALES, JsonTool.toJsonArray(scales.toArray()));
			scales = null;

			// array = new JsonArray();
			List<LightPOJO> lights = lightDAO.getList(session);
			// for (LightPOJO light : lights) {
			// array.add(light.toJson());
			// }
			jsonResponse.add(ParamDefine.LIGHTS, JsonTool.toJsonArray(lights.toArray()));
			lights = null;

			jsonResponse.add(ParamDefine.RESULT, StatusCode.SUCCESS.printStatus());

			return jsonResponse.toString();
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			throw new ProcessException(e);
		}
	}

	/**
	 * List transactions.
	 *
	 * @param page the page
	 * @return the string
	 */
	@GET
	@Path("/listtransaction")
	@Produces(value = { MediaType.APPLICATION_JSON })
	public String listTransactions(@QueryParam(ParamDefine.PAGE) IndexParam page, @QueryParam(ParamDefine.KEYWORD) String keyword) {
		if (page == null) {
			page = new IndexParam("1");
		}
		Session session = (Session) this.httpRequest.getAttribute(ParamDefine.HIBERNATE_SESSION);
		try (TransactionDAO transDAO = AbstractDAO.borrowFromPool(DAOPool.transactionPool)) {
			JsonObject jsonResponse = new JsonObject();

			@SuppressWarnings("unchecked")
			List<TransactionPOJO> list = (List<TransactionPOJO>) this.httpRequest.getSession().getAttribute(ParamDefine.TRANSACTIONS);
			if (page.getValue().equals(1) || list == null) {
				list = new ArrayList<TransactionPOJO>(transDAO.getList(session));
				Collections.sort(list, new Comparator<TransactionPOJO>() {

					@Override
					public int compare(TransactionPOJO o1, TransactionPOJO o2) {
						return o2.getTime().compareTo(o1.getTime());
					}
				});
				this.httpRequest.getSession().setAttribute(ParamDefine.TRANSACTIONS, list);
			}

			if (list.size() > 0) {
				List<TransactionPOJO> tmpList = Helper.sortListByIndex(list, page.getValue(), 10); // Default get 10 items per page
				JsonArray transactions = new JsonArray();
				for (TransactionPOJO transaction : tmpList) {
					JsonObject jsonTransaction = JsonTool.toJsonObject(transaction);
					String photo = transaction.getPhoto();
					if (photo != null && !photo.isEmpty()) {
						String photoUrl = "http://" + this.httpRequest.getServerName() + ":" + this.httpRequest.getServerPort()
						        + this.httpRequest.getContextPath() + "/resource/transaction/" + transaction.getIdTransaction().toString() + "/"
						        + photo;
						jsonTransaction.addProperty(ParamDefine.TRANSACTION_PHOTO, photoUrl);
					}
					ItemPOJO item = transaction.getItem();
					JsonObject jsonItem = JsonTool.toJsonObject(item);
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
			System.err.println(e.getMessage());
			e.printStackTrace();
			throw new ProcessException(e);
		}
	}

	/**
	 * List user.
	 *
	 * @return the string
	 */
	@GET
	@Path("/listuser")
	@Produces(value = { MediaType.APPLICATION_JSON })
	public String listUser() {
		Session session = (Session) this.httpRequest.getAttribute(ParamDefine.HIBERNATE_SESSION);

		try (UserDAO userDAO = AbstractDAO.borrowFromPool(DAOPool.userPool)) {
			JsonObject jsonResponse = new JsonObject();
			JsonArray users = new JsonArray();
			for (UserPOJO user : userDAO.getList(session)) {
				JsonObject jsonUser = JsonTool.toJsonObject(user);
				JsonObject jsonCredit = JsonTool.toJsonObject(user.getCreditAccount());

				users.add(JsonTool.mergeJson(jsonUser, jsonCredit));
			}
			jsonResponse.add(ParamDefine.USERS, users);

			return jsonResponse.toString();
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			throw new ProcessException(e);
		}
	}

	/**
	 * Update transaction.
	 *
	 * @param formData the form data
	 * @return the string
	 */
	@POST
	@Path("/updatetransaction")
	@Consumes(value = { MediaType.MULTIPART_FORM_DATA })
	@Produces(value = { MediaType.APPLICATION_JSON })
	public String updateTransaction(FormDataMultiPart formData) {
		Session session = (Session) this.httpRequest.getAttribute(ParamDefine.HIBERNATE_SESSION);
		FormDataBodyPart fileBody = formData.getField(ParamDefine.TRANSACTION_PHOTO);
		String fileMineType;
		try {
			String fileName = fileBody.getContentDisposition().getFileName();
			fileMineType = fileName.substring(fileName.lastIndexOf("."));
		}
		catch (Exception e) {
			fileMineType = ".png";
		}
		FormDataBodyPart idTransPart = formData.getField(ParamDefine.TRANSACTION_ID);
		Integer idTrans = Integer.valueOf(idTransPart.getValue());

		try (InputStream fileStream = fileBody.getValueAs(InputStream.class);
		        TransactionDAO transDAO = AbstractDAO.borrowFromPool(DAOPool.transactionPool);) {
			JsonObject jsonResponse = new JsonObject();

			byte[] bytes = IOUtils.toByteArray(fileStream);
			final String PREFIX = "stream2file";
			final String SUFFIX = ".tmp";
			File tempFile = File.createTempFile(PREFIX, SUFFIX);

			try (OutputStream fOut = new FileOutputStream(tempFile)) {
				fOut.write(bytes);
				fOut.flush();

				// Check file
				if (ImageChecker.isImage(tempFile)) {
					File directoryTrans = new File(Config.resource_trans_path + idTrans.toString());
					if (!directoryTrans.exists()) {
						directoryTrans.mkdir();
					}
					String nameTrans = System.currentTimeMillis() + fileMineType;
					File fileTrans = new File(directoryTrans.getAbsolutePath() + Constant.SEPERATOR + nameTrans);
					ScaleImage.doScale(tempFile, fileTrans, 320);

					// Write photo success, update transaction in database
					TransactionPOJO trans = transDAO.get(session, idTrans);
					if (trans != null) {
						trans.setPhoto(nameTrans);
						transDAO.update(session, trans);
						HibernateUtil.commitTransaction(session);

						jsonResponse.add(ParamDefine.RESULT, StatusCode.SUCCESS.printStatus());

						// Push this photo url to USER
						String photoUrl = "http://" + this.httpRequest.getServerName() + ":" + this.httpRequest.getServerPort()
						        + this.httpRequest.getContextPath() + "/resource/transaction/" + idTrans.toString() + "/" + nameTrans;

						TransConfirmContent content = new TransConfirmContent(idTrans, photoUrl);
						MessageConfirmTransaction message = new MessageConfirmTransaction();
						message.setCommand(Constant.SOCKET_COMMAND_CONFIRM_TRANS);
						message.setContent(content);
						WSEndpoint userSocket = WSEndpoint._clientSessionMap.get(trans.getUser().getIdUser());
						if (userSocket != null) {
							try {
								userSocket.echoMessage(message);
							}
							catch (Exception e) {
								e.printStackTrace();
							}

						}
						content = null;
						message = null;
						directoryTrans = null;
						fileTrans = null;
						nameTrans = null;
					}
					else {
						jsonResponse.add(ParamDefine.RESULT,
						        StatusCode.BAD_PARAM.printStatus("Cannot find transaction with ID " + idTrans.toString()));

						fileTrans.delete();
						directoryTrans = null;
						fileTrans = null;
						nameTrans = null;
					}

				}
				else {
					jsonResponse.add(ParamDefine.RESULT, StatusCode.BAD_REQUEST.printStatus("File uploaded not image type"));
				}
				return jsonResponse.toString();
			}
			catch (Exception e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
				HibernateUtil.rollbackTransaction(session);
				throw new ProcessException(e);
			}
			finally {
				bytes = null;
				tempFile.delete();
			}
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			throw new ProcessException(e);
		}
		finally {
			fileBody.cleanup();
			fileBody = null;
			fileMineType = null;
			idTransPart.cleanup();
			idTrans = null;
		}
	}
}
