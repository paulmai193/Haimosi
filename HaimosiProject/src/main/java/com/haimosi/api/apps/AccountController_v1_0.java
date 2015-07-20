package com.haimosi.api.apps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import logia.hibernate.dao.AbstractDAO;
import logia.hibernate.util.HibernateUtil;
import logia.utility.filechecker.ImageChecker;
import logia.utility.image.ScaleImage;
import logia.utility.json.JsonTool;
import logia.utility.readfile.FileUtil;
import logia.utility.string.EncryptionUtils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.Session;

import com.google.gson.JsonObject;
import com.haimosi.define.Config;
import com.haimosi.define.Constant;
import com.haimosi.define.StatusCode;
import com.haimosi.exception.BadParamException;
import com.haimosi.exception.ProcessException;
import com.haimosi.hibernate.dao.CreditAccountDAO;
import com.haimosi.hibernate.dao.RoleDAO;
import com.haimosi.hibernate.dao.UserDAO;
import com.haimosi.hibernate.pojo.CreditAccountPOJO;
import com.haimosi.hibernate.pojo.RolePOJO;
import com.haimosi.hibernate.pojo.UserPOJO;
import com.haimosi.param.ContactParam;
import com.haimosi.param.CreditExpireParam;
import com.haimosi.param.IntegerParam;
import com.haimosi.param.ParamDefine;
import com.haimosi.param.StringNotEmptyParam;
import com.haimosi.pool.DAOPool;
import com.haimosi.servlet.filter.AuthenAppsFilter;
import com.haimosi.util.EmailProcess;
import com.haimosi.util.Helper;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;

/**
 * The Class AccountController v1.0.
 * 
 * @author Paul Mai
 */
@Path("/api/v1.0/apps/account")
public class AccountController_v1_0 {

	/** The http request. */
	@Context
	HttpServletRequest httpRequest;

	/**
	 * Activate.
	 *
	 * @param verifyCode the verify code
	 * @param idUser the id user
	 * @return the response
	 */
	@GET
	@Path("/activate")
	@Produces(value = { MediaType.TEXT_PLAIN })
	public Response activate(@QueryParam(ParamDefine.VERIFY_CODE) StringNotEmptyParam verifyCode, @QueryParam(ParamDefine.USER) IntegerParam idUser) {
		ResponseBuilder response = null;
		Session session = (Session) this.httpRequest.getAttribute(ParamDefine.HIBERNATE_SESSION);
		try (UserDAO userDAO = AbstractDAO.borrowFromPool(DAOPool.userPool)) {

			UserPOJO user = userDAO.get(session, idUser.getValue());
			if (user != null) {
				if (user.getStatus() == Constant.USER_STATUS_ACTIVATE || user.getStatus() == Constant.USER_STATUS_INACTIVATE) {
					if (user.getVerifyCode() != null && user.getVerifyCode().equals(verifyCode.getValue())) {
						user.setStatus(Constant.USER_STATUS_ACTIVATE);
						user.setVerifyCode(null);
						userDAO.saveOrUpdate(session, user);
						HibernateUtil.commitTransaction(session);

						response = Response.ok().entity("Congratulation, you have activated Haimosi account success. Enjoy!");
					}
					else {
						response = Response.status(Status.NOT_ACCEPTABLE).entity("Not recognize link!");
					}
				}
				else {
					response = Response.status(Status.NOT_ACCEPTABLE).entity("You was locked!");
				}
			}
			else {
				response = Response.status(Status.NOT_ACCEPTABLE).entity("Not recognize link!");
			}
			user = null;
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			HibernateUtil.rollbackTransaction(session);
			response = Response.status(Status.INTERNAL_SERVER_ERROR).entity("There is some error, please try again later!");
		}
		return response.build();
	}

	/**
	 * Change password.
	 *
	 * @param oldPassword the old password
	 * @param newPassword the new password
	 * @return the string
	 */
	@POST
	@Path("/changepassword")
	@Consumes(value = { MediaType.APPLICATION_FORM_URLENCODED })
	@Produces(value = { MediaType.APPLICATION_JSON })
	public String changePassword(@FormParam(ParamDefine.OLD_PASSWORD) StringNotEmptyParam oldPassword,
	        @FormParam(ParamDefine.NEW_PASSWORD) StringNotEmptyParam newPassword) {
		Session session = (Session) this.httpRequest.getAttribute(ParamDefine.HIBERNATE_SESSION);
		try (UserDAO userDAO = AbstractDAO.borrowFromPool(DAOPool.userPool)) {

			JsonObject jsonResponse = new JsonObject();

			UserPOJO user = (UserPOJO) this.httpRequest.getAttribute(ParamDefine.USER);
			if (user.getPassword().equals(oldPassword.getValue())) {
				user.setPassword(newPassword.getValue());
				userDAO.update(session, user);
				HibernateUtil.commitTransaction(session);

				jsonResponse.add(ParamDefine.RESULT, StatusCode.SUCCESS.printStatus());
			}
			else {
				jsonResponse.add(ParamDefine.RESULT, StatusCode.BAD_REQUEST.printStatus("Current password not match"));
			}
			user = null;
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
	public String editAccount(@FormParam(ParamDefine.CARD_NAME) StringNotEmptyParam cardName,
	        @FormParam(ParamDefine.CARD_NUMBER) StringNotEmptyParam cardNumber, @FormParam(ParamDefine.CVV_NUMBER) StringNotEmptyParam cvvNumber,
	        @FormParam(ParamDefine.EXPIRE) CreditExpireParam expireDay) {
		Session session = (Session) this.httpRequest.getAttribute(ParamDefine.HIBERNATE_SESSION);
		try (CreditAccountDAO creditDAO = AbstractDAO.borrowFromPool(DAOPool.creditPool)) {
			JsonObject jsonResponse = new JsonObject();

			UserPOJO user = (UserPOJO) this.httpRequest.getAttribute(ParamDefine.USER);
			CreditAccountPOJO credit = user.getCreditAccount();
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
	 * Edits the avatar.
	 *
	 * @param formData the form data
	 * @return the string
	 */
	@POST
	@Path("/editavatar")
	@Consumes(value = { MediaType.MULTIPART_FORM_DATA })
	@Produces(value = { MediaType.APPLICATION_JSON })
	public String editAvatar(FormDataMultiPart formData) {
		Session session = (Session) this.httpRequest.getAttribute(ParamDefine.HIBERNATE_SESSION);
		UserPOJO user = (UserPOJO) this.httpRequest.getAttribute(ParamDefine.USER);
		FormDataBodyPart fileBody = formData.getField(ParamDefine.AVATAR);
		String fileMineType;
		try {
			String fileName = fileBody.getContentDisposition().getFileName();
			fileMineType = fileName.substring(fileName.lastIndexOf("."));
		}
		catch (Exception e) {
			fileMineType = ".png";
		}

		try (InputStream fileStream = fileBody.getValueAs(InputStream.class); UserDAO userDAO = AbstractDAO.borrowFromPool(DAOPool.userPool)) {
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
					File directoryAvatar = new File(Config.resource_avatar_path + user.getIdUser().toString());
					if (!directoryAvatar.exists()) {
						directoryAvatar.mkdir();
					}
					String nameAvatar = System.currentTimeMillis() + fileMineType;
					File fileAvatar = new File(directoryAvatar.getAbsolutePath() + Constant.SEPERATOR + nameAvatar);
					ScaleImage.doScale(tempFile, fileAvatar, 320);

					// Write avatar success, update user in database
					user.setAvatar(nameAvatar);
					userDAO.saveOrUpdate(session, user);
					HibernateUtil.commitTransaction(session);

					jsonResponse.add(ParamDefine.RESULT, StatusCode.SUCCESS.printStatus());

					directoryAvatar = null;
					fileAvatar = null;
					nameAvatar = null;
				}
				else {
					jsonResponse.add(ParamDefine.RESULT, StatusCode.BAD_REQUEST.printStatus("File upload not image type"));
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
		}
	}

	/**
	 * Edits the profile.
	 *
	 * @param firstName the first name
	 * @param lastName the last name
	 * @param phone the phone
	 * @param email the email
	 * @return the string
	 */
	@POST
	@Path("/editprofile")
	@Consumes(value = { MediaType.APPLICATION_FORM_URLENCODED })
	@Produces(value = { MediaType.APPLICATION_JSON })
	public String editProfile(@FormParam(ParamDefine.FIRST_NAME) StringNotEmptyParam firstName,
	        @FormParam(ParamDefine.LAST_NAME) StringNotEmptyParam lastName, @FormParam(ParamDefine.PHONE) ContactParam phone,
	        @FormParam(ParamDefine.EMAIL) ContactParam email) {
		Session session = (Session) this.httpRequest.getAttribute(ParamDefine.HIBERNATE_SESSION);
		try (UserDAO userDAO = AbstractDAO.borrowFromPool(DAOPool.userPool)) {
			JsonObject jsonResponse = new JsonObject();

			UserPOJO user = (UserPOJO) this.httpRequest.getAttribute(ParamDefine.USER);
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
	 * Forget password.
	 *
	 * @param email the email
	 * @return the string
	 */
	@POST
	@Path("/forgetpassword")
	@Consumes(value = { MediaType.APPLICATION_FORM_URLENCODED })
	@Produces(value = { MediaType.APPLICATION_JSON })
	public String forgetPassword(@FormParam(ParamDefine.EMAIL) ContactParam email) {
		Session session = (Session) this.httpRequest.getAttribute(ParamDefine.HIBERNATE_SESSION);
		try (UserDAO userDAO = AbstractDAO.borrowFromPool(DAOPool.userPool)) {

			JsonObject jsonResponse = new JsonObject();

			UserPOJO user = userDAO.selectByEmail(session, email.getValue());
			if (user != null) {
				if (user.getStatus() != Constant.USER_STATUS_LOCKED) {

					// Create verify code and save
					String verifyCode = RandomStringUtils.randomNumeric(6);
					user.setVerifyCode(verifyCode);
					userDAO.saveOrUpdate(session, user);
					HibernateUtil.commitTransaction(session);

					// Send verify code to contact
					EmailProcess emailProcess = new EmailProcess();
					if (emailProcess.sendEmail(email.getValue(), "", "Reset Haimosi password", "Enter <b>" + verifyCode
					        + "</b> in Haimosi application and reset your password.")) {
						jsonResponse.add(ParamDefine.RESULT, StatusCode.SUCCESS.printStatus());
					}
					else {
						jsonResponse.add(ParamDefine.RESULT, StatusCode.INTERNAL_ERROR.printStatus());
					}
					emailProcess = null;
				}
				else {
					jsonResponse.add(ParamDefine.RESULT, StatusCode.LOCKED.printStatus());
				}
			}
			else {
				jsonResponse.add(ParamDefine.RESULT, StatusCode.WRONG_ACCOUNT.printStatus());
			}
			user = null;
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
	 * Gets the account.
	 *
	 * @return the account
	 */
	@GET
	@Path("/getaccount")
	@Produces(value = { MediaType.APPLICATION_JSON })
	public String getAccount() {
		JsonObject jsonResponse = new JsonObject();
		try {
			UserPOJO user = (UserPOJO) this.httpRequest.getAttribute(ParamDefine.USER);
			CreditAccountPOJO account = user.getCreditAccount();
			if (account != null) {
				jsonResponse = JsonTool.toJsonObject(account);
				jsonResponse.add(ParamDefine.RESULT, StatusCode.SUCCESS.printStatus());
			}
			else {
				jsonResponse.add(ParamDefine.RESULT, StatusCode.NO_CONTENT.printStatus());
			}
			return jsonResponse.toString();
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			throw new ProcessException(e);
		}
	}

	/**
	 * Gets the profile.
	 *
	 * @return the profile
	 */
	@GET
	@Path("/getprofile")
	@Produces(value = { MediaType.APPLICATION_JSON })
	public String getProfile() {
		JsonObject jsonResponse = new JsonObject();
		try {
			UserPOJO user = (UserPOJO) this.httpRequest.getAttribute(ParamDefine.USER);
			jsonResponse = JsonTool.toJsonObject(user);
			String avatarUrl = "http://" + this.httpRequest.getServerName() + ":" + this.httpRequest.getServerPort()
			        + this.httpRequest.getContextPath() + "/resource/avatar/" + user.getIdUser().toString() + "/" + user.getAvatar();
			jsonResponse.addProperty(ParamDefine.AVATAR, avatarUrl);
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
	 * Login.
	 *
	 * @param email the email
	 * @param password the password
	 * @return the string
	 * @throws Exception the exception
	 */
	@POST
	@Path("/login")
	@Consumes(value = { MediaType.APPLICATION_FORM_URLENCODED })
	@Produces(value = { MediaType.APPLICATION_JSON })
	public String login(@FormParam(ParamDefine.EMAIL) ContactParam email, @FormParam(ParamDefine.PASSWORD) StringNotEmptyParam password)
	        throws Exception {
		Session session = (Session) this.httpRequest.getAttribute(ParamDefine.HIBERNATE_SESSION);
		try (UserDAO userDAO = AbstractDAO.borrowFromPool(DAOPool.userPool)) {

			JsonObject jsonResponse = new JsonObject();

			UserPOJO user = userDAO.selectByEmail(session, email.getValue());
			if (user != null && user.getPassword().equals(password.getValue())) {
				if (user.getStatus() == Constant.USER_STATUS_ACTIVATE) {
					String token = "" + System.currentTimeMillis();
					AuthenAppsFilter._clientMap.put(user.getIdUser(), token);
					this.httpRequest.getSession().setAttribute(ParamDefine.TOKEN, token);
					this.httpRequest.getSession().setAttribute(ParamDefine.USER, user.getIdUser());
					jsonResponse.add(ParamDefine.RESULT, StatusCode.SUCCESS.printStatus());
					jsonResponse.addProperty(ParamDefine.ROLE_ID, user.getRole().getIdRole());

					this.httpRequest.getSession().setMaxInactiveInterval((int) Helper.getRemainTimeInDay() / 1000);
				}
				else if (user.getStatus() == Constant.USER_STATUS_INACTIVATE) {
					jsonResponse.add(ParamDefine.RESULT, StatusCode.MUST_VERIFY.printStatus());
				}
				else {
					jsonResponse.add(ParamDefine.RESULT, StatusCode.LOCKED.printStatus());
				}
			}
			else {
				jsonResponse.add(ParamDefine.RESULT, StatusCode.WRONG_ACCOUNT.printStatus());
			}
			user = null;
			return jsonResponse.toString();
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			throw new ProcessException(e);
		}
	}

	/**
	 * Register.
	 *
	 * @param firstName the first name
	 * @param lastName the last name
	 * @param phone the phone
	 * @param email the email
	 * @param password the password
	 * @param cardName the card name
	 * @param cardNumber the card number
	 * @param cvvNumber the cvv number
	 * @param expireDay the expire day
	 * @return the string
	 * @throws Exception the exception
	 */
	@POST
	@Path("/register")
	@Consumes(value = { MediaType.APPLICATION_FORM_URLENCODED })
	@Produces(value = { MediaType.APPLICATION_JSON })
	public String register(@FormParam(ParamDefine.FIRST_NAME) StringNotEmptyParam firstName,
	        @FormParam(ParamDefine.LAST_NAME) StringNotEmptyParam lastName, @FormParam(ParamDefine.PHONE) ContactParam phone,
	        @FormParam(ParamDefine.EMAIL) ContactParam email, @FormParam(ParamDefine.PASSWORD) StringNotEmptyParam password,
	        @FormParam(ParamDefine.CARD_NAME) StringNotEmptyParam cardName, @FormParam(ParamDefine.CARD_NUMBER) StringNotEmptyParam cardNumber,
	        @FormParam(ParamDefine.CVV_NUMBER) StringNotEmptyParam cvvNumber, @FormParam(ParamDefine.EXPIRE) CreditExpireParam expireDay)
	        throws Exception {
		// Check all register information available
		if (firstName == null || lastName == null || phone == null || email == null || password == null || cardName == null || cardNumber == null
		        || cvvNumber == null || expireDay == null) {
			throw new BadParamException(new Throwable("Some of required fields empty"));
		}

		// Check contact parameter have true pattern
		if (email.getContactType() != ContactParam.CONTACT_EMAIL || phone.getContactType() != ContactParam.CONTACT_PHONE) {
			throw new BadParamException();
		}
		Session session = (Session) this.httpRequest.getAttribute(ParamDefine.HIBERNATE_SESSION);
		try (CreditAccountDAO creditDAO = AbstractDAO.borrowFromPool(DAOPool.creditPool);
		        UserDAO userDAO = AbstractDAO.borrowFromPool(DAOPool.userPool);
		        RoleDAO roleDAO = AbstractDAO.borrowFromPool(DAOPool.rolePool)) {

			JsonObject jsonResponse = new JsonObject();

			// Check email account exist
			UserPOJO user = userDAO.selectByEmail(session, email.getValue());
			if (user != null) {
				jsonResponse.add(ParamDefine.RESULT, StatusCode.EXIST_ACCOUNT.printStatus());
			}
			else {
				// Save credit account first
				CreditAccountPOJO creditAccount = new CreditAccountPOJO(null, cardName.getValue(), cardNumber.getValue(), expireDay.getValue(),
				        cvvNumber.getValue());
				Integer idCredit = creditDAO.saveID(session, creditAccount);

				if (idCredit != null) {
					creditAccount.setIdCreditAccount(idCredit);

					// Success, save user next
					RolePOJO role = roleDAO.get(session, Constant.USER_ROLE_MEMBER);
					String verifyCode = RandomStringUtils.randomNumeric(6);
					user = new UserPOJO(null, firstName.getValue(), lastName.getValue(), phone.getValue(), email.getValue(), password.getValue(),
					        verifyCode, Constant.USER_STATUS_INACTIVATE, role, creditAccount, null);
					Integer idUser = userDAO.saveID(session, user);
					if (idUser != null) {
						// Try to re-add user credit account
						user.setIdUser(idUser);
						creditAccount.setUser(user);
						creditDAO.update(session, creditAccount);

						String token = "" + System.currentTimeMillis();
						AuthenAppsFilter._clientMap.put(user.getIdUser(), token);
						this.httpRequest.getSession().setAttribute(ParamDefine.TOKEN, token);
						this.httpRequest.getSession().setAttribute(ParamDefine.USER, idUser);

						// Setup first time use after register, allow using app without login
						this.httpRequest.getSession().setAttribute(ParamDefine.FIRST_TIME_USE, Boolean.TRUE);

						jsonResponse.add(ParamDefine.RESULT, StatusCode.SUCCESS.printStatus());

						this.httpRequest.getSession().setMaxInactiveInterval((int) Helper.getRemainTimeInDay() / 1000);

						// Generate activation link and send to register account
						String nameRecipients = firstName.getValue() + " " + lastName.getValue();
						String enVerifyCode = EncryptionUtils.encode(verifyCode, Config.encrypt_password);
						String link = "http://" + this.httpRequest.getServerName() + ":" + this.httpRequest.getServerPort()
						        + this.httpRequest.getContextPath() + "/link?a=activation&s=" + enVerifyCode + "&i=" + idUser.toString();

						String content;
						try {
							content = FileUtil.readFile(Config.resource_template_path + "ActivateAccount").replace("<name>", nameRecipients)
							        .replace("<link>", link);
							EmailProcess emailProcess = new EmailProcess();
							emailProcess.sendEmail(email.getValue(), nameRecipients, "Verify Haimosi account", content);
						}
						catch (Exception e) {
							System.err.println(e.getMessage());
							e.printStackTrace();
						}

					}
					else {
						jsonResponse.add(ParamDefine.RESULT, StatusCode.INTERNAL_ERROR.printStatus("Cannot register new user"));
					}
					role = null;
					user = null;
				}
				else {
					jsonResponse.add(ParamDefine.RESULT, StatusCode.INTERNAL_ERROR.printStatus("Cannot register new credit account"));
				}

				HibernateUtil.commitTransaction(session);
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
	 * Reset password.
	 *
	 * @param email the email
	 * @param verifyCode the verify code
	 * @param newPassword the new password
	 * @return the string
	 */
	@POST
	@Path("/resetpassword")
	@Consumes(value = { MediaType.APPLICATION_FORM_URLENCODED })
	@Produces(value = { MediaType.APPLICATION_JSON })
	public String resetPassword(@FormParam(ParamDefine.EMAIL) ContactParam email, @FormParam(ParamDefine.VERIFY_CODE) StringNotEmptyParam verifyCode,
	        @FormParam(ParamDefine.NEW_PASSWORD) StringNotEmptyParam newPassword) {
		Session session = (Session) this.httpRequest.getAttribute(ParamDefine.HIBERNATE_SESSION);
		try (UserDAO userDAO = AbstractDAO.borrowFromPool(DAOPool.userPool)) {

			JsonObject jsonResponse = new JsonObject();

			UserPOJO user = userDAO.selectByEmail(session, email.getValue());
			if (user != null) {
				if (user.getStatus() == Constant.USER_STATUS_ACTIVATE || user.getStatus() == Constant.USER_STATUS_INACTIVATE) {
					if (user.getVerifyCode() != null && user.getVerifyCode().equals(verifyCode.getValue())) {
						user.setPassword(newPassword.getValue());
						user.setStatus(Constant.USER_STATUS_ACTIVATE);
						user.setVerifyCode(null);
						userDAO.saveOrUpdate(session, user);
						HibernateUtil.commitTransaction(session);

						jsonResponse.add(ParamDefine.RESULT, StatusCode.SUCCESS.printStatus());
					}
					else {
						jsonResponse.add(ParamDefine.RESULT, StatusCode.BAD_VERIFY_CODE.printStatus());
					}
				}
				else {
					jsonResponse.add(ParamDefine.RESULT, StatusCode.LOCKED.printStatus());
				}
			}
			else {
				jsonResponse.add(ParamDefine.RESULT, StatusCode.WRONG_ACCOUNT.printStatus());
			}
			user = null;
			return jsonResponse.toString();
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			HibernateUtil.rollbackTransaction(session);
			throw new ProcessException(e);
		}
	}

}