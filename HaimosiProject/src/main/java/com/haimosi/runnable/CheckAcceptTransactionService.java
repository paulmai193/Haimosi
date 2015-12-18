package com.haimosi.runnable;

import logia.hibernate.dao.AbstractDAO;
import logia.hibernate.util.HibernateUtil;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.haimosi.define.Constant;
import com.haimosi.hibernate.dao.TransactionDAO;
import com.haimosi.hibernate.pojo.TransactionPOJO;
import com.haimosi.pool.DAOPool;

public class CheckAcceptTransactionService implements Runnable {

	private final Logger LOGGER = Logger.getLogger(getClass());
	private Integer      idTransaction;

	public CheckAcceptTransactionService(Integer idTransaction) {
		this.idTransaction = idTransaction;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(5 * 60 * 1000);
		}
		catch (InterruptedException e1) {
			// Swallow this exception
		}
		String log = "Check transaction " + this.idTransaction + " status\n";
		Session session = HibernateUtil.beginTransaction();
		try (TransactionDAO transDAO = AbstractDAO.borrowFromPool(DAOPool.transactionPool)) {
			TransactionPOJO checkTrans = transDAO.get(session, idTransaction);
			if (checkTrans != null) {
				if (checkTrans.getStatus() == Constant.TRANS_STATUS_WAIT) {
					checkTrans.setStatus(Constant.TRANS_STATUS_DENY);
					transDAO.update(session, checkTrans);

					HibernateUtil.commitTransaction(session);
					log += "Deny transaction after 5 minutes\n";
				}
				else {
					log += "Transaction was accepted\n";
				}
			}
			else {
				log += "Transaction not exist\n";
			}
		}
		catch (Exception e) {
			this.LOGGER.error(e.getMessage(), e);
			HibernateUtil.rollbackTransaction(session);
		}
		finally {
			HibernateUtil.closeSession(session);
			this.LOGGER.info(log);
		}

	}

}
