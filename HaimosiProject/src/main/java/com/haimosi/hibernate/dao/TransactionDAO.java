package com.haimosi.hibernate.dao;

import logia.hibernate.dao.AbstractDAO;

import org.hibernate.Session;

import com.haimosi.hibernate.pojo.TransactionPOJO;
import com.haimosi.pool.DAOPool;

/**
 * The Class TransactionDAO.
 * 
 * @author Paul Mai
 */
public class TransactionDAO extends AbstractDAO<TransactionPOJO, Integer> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.AutoCloseable#close()
	 */
	@Override
	public void close() throws Exception {
		DAOPool.transactionPool.returnObject(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.hibernate.dao.AbstractDAO#getPOJOClass()
	 */
	@Override
	protected Class<TransactionPOJO> getPOJOClass() {
		return TransactionPOJO.class;
	}

	/**
	 * Gets the by id charge.
	 *
	 * @param session the session
	 * @param idCharge the id charge
	 * @return the by id charge
	 */
	public TransactionPOJO getByIdCharge(Session session, String idCharge) {
		String queryString = "from TransactionPOJO where idCharge = '" + idCharge + "'";
		return selectUniqueByQuery(session, queryString);
	}
}
