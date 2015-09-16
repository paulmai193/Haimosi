package com.haimosi.api.apps.thirdparty;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import logia.hibernate.dao.AbstractDAO;
import logia.hibernate.util.HibernateUtil;
import logia.utility.json.JsonUtil;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.google.gson.JsonParser;
import com.haimosi.api.apps.thirdparty.data.StripeEvent;
import com.haimosi.api.apps.thirdparty.data.StripeRefundData;
import com.haimosi.define.Constant;
import com.haimosi.exception.ProcessException;
import com.haimosi.hibernate.dao.TransactionDAO;
import com.haimosi.hibernate.pojo.TransactionPOJO;
import com.haimosi.pool.DAOPool;

/**
 * The Class StripeEventListenter.
 * 
 * @author Paul Mai
 */
@Path("/api/v1.0/3rdparty/stripe")
public class StripeEventListenter {

	/** The logger. */
	private final Logger LOGGER = Logger.getLogger(this.getClass());

	/**
	 * Refund transaction.
	 */
	@POST
	@Path("/refund")
	@Consumes(value = { MediaType.APPLICATION_JSON })
	public void refundTransaction(String requestEvent) {
		this.LOGGER.info("Test refund: " + requestEvent);
		StripeEvent eventRefund = JsonUtil.fromJsonObject(new JsonParser().parse(requestEvent).getAsJsonObject(), StripeEvent.class);
		this.LOGGER.info(eventRefund.getId());
		StripeRefundData refundData = eventRefund.getData().getRefund();
		String idCharge = refundData.getIdCharge();

		Session session = HibernateUtil.beginTransaction();

		try (TransactionDAO transactionDAO = AbstractDAO.borrowFromPool(DAOPool.transactionPool)) {
			TransactionPOJO transaction = transactionDAO.getByIdCharge(session, idCharge);
			if (transaction != null) {
				transaction.setStatus(Constant.TRANS_REFUND);
				HibernateUtil.commitTransaction(session);
			}
		}
		catch (Exception e) {
			this.LOGGER.error(e.getMessage(), e);
			HibernateUtil.rollbackTransaction(session);
			throw new ProcessException(e);
		}
		finally {
			HibernateUtil.closeSession(session);
		}
	}
}
