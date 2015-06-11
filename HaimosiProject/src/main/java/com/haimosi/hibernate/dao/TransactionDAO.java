package com.haimosi.hibernate.dao;

import logia.hibernate.dao.AbstractDAO;

import com.haimosi.hibernate.pojo.TransactionPOJO;

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

}
