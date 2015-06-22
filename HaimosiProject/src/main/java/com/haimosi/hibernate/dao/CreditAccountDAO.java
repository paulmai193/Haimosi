package com.haimosi.hibernate.dao;

import logia.hibernate.dao.AbstractDAO;

import com.haimosi.hibernate.pojo.CreditAccountPOJO;
import com.haimosi.pool.DAOPool;

/**
 * The Class CreditAccountDAO.
 * 
 * @author Paul Mai
 */
public class CreditAccountDAO extends AbstractDAO<CreditAccountPOJO, Integer> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.AutoCloseable#close()
	 */
	@Override
	public void close() throws Exception {
		DAOPool.creditPool.returnObject(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.hibernate.dao.AbstractDAO#getPOJOClass()
	 */
	@Override
	protected Class<CreditAccountPOJO> getPOJOClass() {
		return CreditAccountPOJO.class;
	}

}
